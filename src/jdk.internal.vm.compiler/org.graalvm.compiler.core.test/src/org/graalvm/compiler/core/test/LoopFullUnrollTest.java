/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.core.test;

import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.debug.DebugDumpScope;
import org.graalvm.compiler.loop.DefaultLoopPolicies;
import org.graalvm.compiler.loop.phases.LoopFullUnrollPhase;
import org.graalvm.compiler.nodes.LoopBeginNode;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.StructuredGraph.AllowAssumptions;
import org.graalvm.compiler.nodes.spi.CoreProviders;
import org.graalvm.compiler.phases.common.CanonicalizerPhase;
import org.junit.Test;

public class LoopFullUnrollTest extends GraalCompilerTest {

    public static int testMinToMax(int input) {
        int ret = 2;
        int current = input;
        for (long i = Long.MIN_VALUE; i < Long.MAX_VALUE; i++) {
            ret *= 2 + current;
            current /= 50;
        }
        return ret;
    }

    @Test
    public void runMinToMax() throws Throwable {
        test("testMinToMax", 1);
    }

    public static int testMinTo0(int input) {
        int ret = 2;
        int current = input;
        for (long i = Long.MIN_VALUE; i <= 0; i++) {
            ret *= 2 + current;
            current /= 50;
        }
        return ret;
    }

    @Test
    public void runMinTo0() throws Throwable {
        test("testMinTo0", 1);
    }

    public static int testNegativeTripCount(int input) {
        int ret = 2;
        int current = input;
        for (long i = 0; i <= -20; i++) {
            ret *= 2 + current;
            current /= 50;
        }
        return ret;
    }

    @Test
    public void runNegativeTripCount() throws Throwable {
        test("testNegativeTripCount", 0);
    }

    @SuppressWarnings("try")
    private void test(String snippet, int loopCount) {
        DebugContext debug = getDebugContext();
        try (DebugContext.Scope s = debug.scope(getClass().getSimpleName(), new DebugDumpScope(snippet))) {
            final StructuredGraph graph = parseEager(snippet, AllowAssumptions.NO, debug);

            CoreProviders context = getProviders();
            new LoopFullUnrollPhase(new CanonicalizerPhase(), new DefaultLoopPolicies()).apply(graph, context);

            assertTrue(graph.getNodes().filter(LoopBeginNode.class).count() == loopCount);
        } catch (Throwable e) {
            throw debug.handle(e);
        }
    }
}
