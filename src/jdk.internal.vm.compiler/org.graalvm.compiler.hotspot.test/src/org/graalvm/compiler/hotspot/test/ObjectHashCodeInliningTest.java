/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.test;

import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.core.test.GraalCompilerTest;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.extended.ForeignCallNode;
import org.graalvm.compiler.nodes.java.MethodCallTargetNode;
import org.graalvm.compiler.nodes.memory.ReadNode;
import org.junit.Assume;
import org.junit.Test;

public class ObjectHashCodeInliningTest extends GraalCompilerTest {

    public static int getHash(Object obj) {
        return obj.hashCode();
    }

    @Test
    public void testInstallCodeInvalidation() {
        for (int i = 0; i < 100000; i++) {
            getHash(i % 10 == 0 ? new Object() : "");
        }

        ResolvedJavaMethod method = getResolvedJavaMethod("getHash");
        StructuredGraph graph = parseForCompile(method);
        for (MethodCallTargetNode callTargetNode : graph.getNodes(MethodCallTargetNode.TYPE)) {
            if ("Object.hashCode".equals(callTargetNode.targetName())) {
                Assume.assumeTrue(callTargetNode.getProfile() != null);
            }
        }
        compile(method, graph);
    }

    private static boolean containsForeignCallToIdentityHashCode(StructuredGraph graph) {
        for (ForeignCallNode foreignCallNode : graph.getNodes().filter(ForeignCallNode.class)) {
            if ("identity_hashcode".equals(foreignCallNode.getDescriptor().getName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsReadStringHash(StructuredGraph graph) {
        for (ReadNode readNode : graph.getNodes().filter(ReadNode.class)) {
            if ("String.hash".equals(readNode.getLocationIdentity().toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void checkHighTierGraph(StructuredGraph graph) {
        assert containsForeignCallToIdentityHashCode(graph) : "expected a foreign call to identity_hashcode";
        assert containsReadStringHash(graph) : "expected a read from String.hash";
    }

}
