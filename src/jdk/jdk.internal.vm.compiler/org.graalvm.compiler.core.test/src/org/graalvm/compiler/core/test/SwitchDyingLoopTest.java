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


package org.graalvm.compiler.core.test;

import jdk.vm.ci.meta.JavaKind;
import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.nodes.LoopBeginNode;
import org.graalvm.compiler.nodes.ParameterNode;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.extended.IntegerSwitchNode;
import org.graalvm.compiler.phases.common.CanonicalizerPhase;
import org.graalvm.compiler.phases.tiers.HighTierContext;
import org.junit.Test;

import static org.graalvm.compiler.graph.test.matchers.NodeIterableCount.hasCount;
import static org.graalvm.compiler.graph.test.matchers.NodeIterableIsEmpty.isEmpty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SwitchDyingLoopTest extends GraalCompilerTest {

    @SuppressWarnings("fallthrough")
    public static int snippet(int a, int n) {
        int r = 0;
        loop: for (int i = 0; i < n; i++) {
            int v = (i * 167 + 13) & 0xff;
            switch (v & a) {
                case 0x80:
                    r += 1; // fall through
                case 0x40:
                    r += 2; // fall through
                case 0x20:
                    r += 3;
                    continue;
                case 0x08:
                    r += 5; // fall through
                case 0x04:
                    r += 7; // fall through
                case 0x02:
                    r += 9; // fall through
                default:
                    break loop;
            }
        }
        return r;
    }

    @Test
    public void test() {
        CanonicalizerPhase canonicalizerPhase = new CanonicalizerPhase();
        HighTierContext highTierContext = getDefaultHighTierContext();
        StructuredGraph graph = parseEager("snippet", StructuredGraph.AllowAssumptions.YES);
        // there should be 1 loop and 1 switch
        assertThat(graph.getNodes(LoopBeginNode.TYPE), hasCount(1));
        assertThat(graph.getNodes(IntegerSwitchNode.TYPE), hasCount(1));
        canonicalizerPhase.apply(graph, highTierContext);
        // after canonicalization, the loop and switch should still be there
        assertThat(graph.getNodes(LoopBeginNode.TYPE), hasCount(1));
        assertThat(graph.getNodes(IntegerSwitchNode.TYPE), hasCount(1));
        // add stamp to `a` so that paths leading to continue can be trimmed
        ParameterNode parameter = graph.getParameter(0);
        assertNotNull(parameter);
        parameter.setStamp(StampFactory.forInteger(JavaKind.Int, 0, 255, 0, 0xf));
        canonicalizerPhase.apply(graph, highTierContext);
        // the loop should have disappeared and there should still be a switch
        assertThat(graph.getNodes(LoopBeginNode.TYPE), isEmpty());
        assertThat(graph.getNodes(IntegerSwitchNode.TYPE), hasCount(1));
    }
}