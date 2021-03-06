/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */


package org.graalvm.compiler.replacements.jdk9.test;

import jdk.internal.vm.compiler.word.LocationIdentity;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.core.test.GraalCompilerTest;
import org.graalvm.compiler.debug.GraalError;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.nodes.StartNode;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.extended.MembarNode;
import org.graalvm.compiler.nodes.memory.MemoryCheckpoint;
import org.graalvm.compiler.nodes.memory.ReadNode;
import org.graalvm.compiler.nodes.memory.WriteNode;
import org.junit.Assert;
import org.junit.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class VarHandleTest extends GraalCompilerTest {

    static class Holder {
        /* Field is declared volatile, but accessed with non-volatile semantics in the tests. */
        volatile int volatileField = 42;

        /* Field is declared non-volatile, but accessed with volatile semantics in the tests. */
        int field = 2018;

        static final VarHandle VOLATILE_FIELD;
        static final VarHandle FIELD;

        static {
            try {
                VOLATILE_FIELD = MethodHandles.lookup().findVarHandle(Holder.class, "volatileField", int.class);
                FIELD = MethodHandles.lookup().findVarHandle(Holder.class, "field", int.class);
            } catch (ReflectiveOperationException ex) {
                throw GraalError.shouldNotReachHere(ex);
            }
        }
    }

    public static int testRead1Snippet(Holder h) {
        /* Explicitly access the volatile field with non-volatile access semantics. */
        return (int) Holder.VOLATILE_FIELD.get(h);
    }

    public static int testRead2Snippet(Holder h) {
        /* Explicitly access the volatile field with volatile access semantics. */
        return (int) Holder.VOLATILE_FIELD.getVolatile(h);
    }

    public static int testRead3Snippet(Holder h) {
        /* Explicitly access the non-volatile field with non-volatile access semantics. */
        return (int) Holder.FIELD.get(h);
    }

    public static int testRead4Snippet(Holder h) {
        /* Explicitly access the non-volatile field with volatile access semantics. */
        return (int) Holder.FIELD.getVolatile(h);
    }

    public static void testWrite1Snippet(Holder h) {
        /* Explicitly access the volatile field with non-volatile access semantics. */
        Holder.VOLATILE_FIELD.set(h, 123);
    }

    public static void testWrite2Snippet(Holder h) {
        /* Explicitly access the volatile field with volatile access semantics. */
        Holder.VOLATILE_FIELD.setVolatile(h, 123);
    }

    public static void testWrite3Snippet(Holder h) {
        /* Explicitly access the non-volatile field with non-volatile access semantics. */
        Holder.FIELD.set(h, 123);
    }

    public static void testWrite4Snippet(Holder h) {
        /* Explicitly access the non-volatile field with volatile access semantics. */
        Holder.FIELD.setVolatile(h, 123);
    }

    void testAccess(String name, int expectedReads, int expectedWrites, int expectedMembars, int expectedAnyKill) {
        ResolvedJavaMethod method = getResolvedJavaMethod(name);
        StructuredGraph graph = parseForCompile(method);
        compile(method, graph);
        Assert.assertEquals(expectedReads, graph.getNodes().filter(ReadNode.class).count());
        Assert.assertEquals(expectedWrites, graph.getNodes().filter(WriteNode.class).count());
        Assert.assertEquals(expectedMembars, graph.getNodes().filter(MembarNode.class).count());
        Assert.assertEquals(expectedAnyKill, countAnyKill(graph));
    }

    @Test
    public void testRead1() {
        testAccess("testRead1Snippet", 1, 0, 0, 0);
    }

    @Test
    public void testRead2() {
        testAccess("testRead2Snippet", 1, 0, 2, 2);
    }

    @Test
    public void testRead3() {
        testAccess("testRead3Snippet", 1, 0, 0, 0);
    }

    @Test
    public void testRead4() {
        testAccess("testRead4Snippet", 1, 0, 2, 2);
    }

    @Test
    public void testWrite1() {
        testAccess("testWrite1Snippet", 0, 1, 0, 0);
    }

    @Test
    public void testWrite2() {
        testAccess("testWrite2Snippet", 0, 1, 2, 2);
    }

    @Test
    public void testWrite3() {
        testAccess("testWrite3Snippet", 0, 1, 0, 0);
    }

    @Test
    public void testWrite4() {
        testAccess("testWrite4Snippet", 0, 1, 2, 2);
    }

    private static int countAnyKill(StructuredGraph graph) {
        int anyKillCount = 0;
        int startNodes = 0;
        for (Node n : graph.getNodes()) {
            if (n instanceof StartNode) {
                startNodes++;
            } else if (n instanceof MemoryCheckpoint.Single) {
                MemoryCheckpoint.Single single = (MemoryCheckpoint.Single) n;
                if (single.getLocationIdentity().isAny()) {
                    anyKillCount++;
                }
            } else if (n instanceof MemoryCheckpoint.Multi) {
                MemoryCheckpoint.Multi multi = (MemoryCheckpoint.Multi) n;
                for (LocationIdentity loc : multi.getLocationIdentities()) {
                    if (loc.isAny()) {
                        anyKillCount++;
                        break;
                    }
                }
            }
        }
        // Ignore single StartNode.
        Assert.assertEquals(1, startNodes);
        return anyKillCount;
    }
}
