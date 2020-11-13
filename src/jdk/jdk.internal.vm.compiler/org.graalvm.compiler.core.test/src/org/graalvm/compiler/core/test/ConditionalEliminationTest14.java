/*
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
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

import jdk.vm.ci.meta.DeoptimizationReason;
import org.graalvm.compiler.graph.iterators.NodeIterable;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.StructuredGraph.AllowAssumptions;
import org.graalvm.compiler.nodes.calc.IntegerBelowNode;
import org.graalvm.compiler.nodes.java.LoadIndexedNode;
import org.graalvm.compiler.nodes.memory.FloatingReadNode;
import org.graalvm.compiler.nodes.memory.ReadNode;
import org.graalvm.compiler.nodes.spi.CoreProviders;
import org.graalvm.compiler.nodes.spi.LoweringTool;
import org.graalvm.compiler.phases.common.CanonicalizerPhase;
import org.graalvm.compiler.phases.common.FloatingReadPhase;
import org.graalvm.compiler.phases.common.IterativeConditionalEliminationPhase;
import org.graalvm.compiler.phases.common.LoweringPhase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Check that multiple bounds checks are correctly grouped together.
 */
public class ConditionalEliminationTest14 extends ConditionalEliminationTestBase {

    public static void test1Snippet(Object[] args) {
        Object a5 = args[5];
        Object a7 = args[7];
        Object a6 = args[6];

        /*
         * The order of the conditions matters: The scheduler processes the floating reads for the
         * array loads in the order of the conditions here, and we want the index 7 access to be
         * processed before the index 6 access.
         */
        if (a5 != null && a7 != null && a6 != null) {
            sink1 = 1;
        }
        sink0 = 0;
    }

    @Test
    public void test1() {
        StructuredGraph graph = parseEager("test1Snippet", AllowAssumptions.YES);
        CanonicalizerPhase canonicalizer = new CanonicalizerPhase();
        CoreProviders context = getProviders();

        /* Convert the LoadIndexNode to ReadNode with floating guards. */
        new LoweringPhase(canonicalizer, LoweringTool.StandardLoweringStage.HIGH_TIER).apply(graph, context);
        /* Convert the ReadNode to FloatingReadNode. */
        new FloatingReadPhase().apply(graph);
        /* Apply the phase that we want to test. */
        new IterativeConditionalEliminationPhase(canonicalizer, true).apply(graph, context);

        Assert.assertEquals("All guards must be floating", 0, graph.getNodes(FixedGuardNode.TYPE).count());
        Assert.assertEquals("All array accesses must have been lowered", 0, graph.getNodes().filter(LoadIndexedNode.class).count());
        Assert.assertEquals("All reads must be floating", 0, graph.getNodes().filter(ReadNode.class).count());
        Assert.assertEquals("Must have floating reads (3 array accesses, 1 array length)", 4, graph.getNodes().filter(FloatingReadNode.class).count());

        NodeIterable<GuardNode> boundsChecks = graph.getNodes(GuardNode.TYPE).filter(n -> ((GuardNode) n).getReason() == DeoptimizationReason.BoundsCheckException);
        Assert.assertEquals("Must have only 1 bounds check remaining", 1, boundsChecks.count());
        LogicNode condition = boundsChecks.first().getCondition();
        Assert.assertTrue("Bounds check must check for array length 8", condition instanceof IntegerBelowNode && ((IntegerBelowNode) condition).getY().valueEquals(ConstantNode.forInt(8)));
    }
}