/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.loop.phases;

import jdk.internal.vm.compiler.collections.EconomicMap;
import jdk.internal.vm.compiler.collections.Equivalence;
import org.graalvm.compiler.graph.Graph;
import org.graalvm.compiler.loop.LoopEx;
import org.graalvm.compiler.loop.LoopPolicies;
import org.graalvm.compiler.loop.LoopsData;
import org.graalvm.compiler.nodes.LoopBeginNode;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.extended.OpaqueNode;
import org.graalvm.compiler.nodes.spi.CoreProviders;
import org.graalvm.compiler.phases.common.CanonicalizerPhase;
import org.graalvm.compiler.phases.common.util.EconomicSetNodeEventListener;

public class LoopPartialUnrollPhase extends LoopPhase<LoopPolicies> {

    private final CanonicalizerPhase canonicalizer;

    public LoopPartialUnrollPhase(LoopPolicies policies, CanonicalizerPhase canonicalizer) {
        super(policies);
        this.canonicalizer = canonicalizer;
    }

    @Override
    @SuppressWarnings("try")
    protected void run(StructuredGraph graph, CoreProviders context) {
        if (graph.hasLoops()) {
            EconomicSetNodeEventListener listener = new EconomicSetNodeEventListener();
            boolean changed = true;
            EconomicMap<LoopBeginNode, OpaqueNode> opaqueUnrolledStrides = null;
            while (changed) {
                changed = false;
                try (Graph.NodeEventScope nes = graph.trackNodeEvents(listener)) {
                    LoopsData dataCounted = new LoopsData(graph);
                    dataCounted.detectedCountedLoops();
                    Graph.Mark mark = graph.getMark();
                    boolean prePostInserted = false;
                    for (LoopEx loop : dataCounted.countedLoops()) {
                        if (!LoopTransformations.isUnrollableLoop(loop)) {
                            continue;
                        }
                        if (getPolicies().shouldPartiallyUnroll(loop)) {
                            if (loop.loopBegin().isSimpleLoop()) {
                                // First perform the pre/post transformation and do the partial
                                // unroll when we come around again.
                                LoopTransformations.insertPrePostLoops(loop);
                                prePostInserted = true;
                            } else {
                                if (opaqueUnrolledStrides == null) {
                                    opaqueUnrolledStrides = EconomicMap.create(Equivalence.IDENTITY);
                                }
                                LoopTransformations.partialUnroll(loop, opaqueUnrolledStrides);
                            }
                            changed = true;
                        }
                    }
                    dataCounted.deleteUnusedNodes();

                    if (!listener.getNodes().isEmpty()) {
                        canonicalizer.applyIncremental(graph, context, listener.getNodes());
                        listener.getNodes().clear();
                    }

                    assert !prePostInserted || checkCounted(graph, mark);
                }
            }
            if (opaqueUnrolledStrides != null) {
                try (Graph.NodeEventScope nes = graph.trackNodeEvents(listener)) {
                    for (OpaqueNode opaque : opaqueUnrolledStrides.getValues()) {
                        opaque.remove();
                    }
                    if (!listener.getNodes().isEmpty()) {
                        canonicalizer.applyIncremental(graph, context, listener.getNodes());
                    }
                }
            }
        }
    }

    private static boolean checkCounted(StructuredGraph graph, Graph.Mark mark) {
        LoopsData dataCounted;
        dataCounted = new LoopsData(graph);
        dataCounted.detectedCountedLoops();
        for (LoopEx anyLoop : dataCounted.loops()) {
            if (graph.isNew(mark, anyLoop.loopBegin())) {
                assert anyLoop.isCounted() : "pre/post transformation loses counted loop " + anyLoop.loopBegin();
            }
        }
        return true;
    }

    @Override
    public boolean checkContract() {
        return false;
    }
}
