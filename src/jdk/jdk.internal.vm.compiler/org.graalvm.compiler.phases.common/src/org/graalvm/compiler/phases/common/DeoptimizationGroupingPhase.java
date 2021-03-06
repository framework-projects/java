/*
 * Copyright (c) 2013, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.phases.common;

import org.graalvm.compiler.core.common.cfg.Loop;
import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.debug.DebugCloseable;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.cfg.Block;
import org.graalvm.compiler.nodes.cfg.ControlFlowGraph;
import org.graalvm.compiler.phases.BasePhase;
import org.graalvm.compiler.phases.tiers.MidTierContext;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This phase tries to find {@link AbstractDeoptimizeNode DeoptimizeNodes} which use the same
 * {@link FrameState} and merges them together.
 */
public class DeoptimizationGroupingPhase extends BasePhase<MidTierContext> {

    @Override
    @SuppressWarnings("try")
    protected void run(StructuredGraph graph, MidTierContext context) {
        ControlFlowGraph cfg = null;
        for (FrameState fs : graph.getNodes(FrameState.TYPE)) {
            Iterator<AbstractDeoptimizeNode> iterator = fs.usages().filter(AbstractDeoptimizeNode.class).iterator();
            if (!iterator.hasNext()) {
                // No deopt
                continue;
            }
            AbstractDeoptimizeNode first = iterator.next();
            if (!iterator.hasNext()) {
                // Only 1 deopt
                continue;
            }
            // There is more than one deopt, create a merge
            if (cfg == null) {
                cfg = ControlFlowGraph.compute(graph, true, true, false, false);
            }
            AbstractMergeNode merge = graph.add(new MergeNode());
            EndNode firstEnd = graph.add(new EndNode());
            ValueNode actionAndReason = first.getActionAndReason(context.getMetaAccess());
            ValueNode speculation = first.getSpeculation(context.getMetaAccess());
            PhiNode reasonActionPhi = graph.addWithoutUnique(new ValuePhiNode(StampFactory.forKind(actionAndReason.getStackKind()), merge));
            PhiNode speculationPhi = graph.addWithoutUnique(new ValuePhiNode(StampFactory.forKind(speculation.getStackKind()), merge));
            merge.addForwardEnd(firstEnd);
            reasonActionPhi.addInput(actionAndReason);
            speculationPhi.addInput(speculation);
            first.replaceAtPredecessor(firstEnd);
            exitLoops(first, firstEnd, cfg);
            DynamicDeoptimizeNode dynamicDeopt;
            try (DebugCloseable position = first.withNodeSourcePosition()) {
                dynamicDeopt = new DynamicDeoptimizeNode(reasonActionPhi, speculationPhi);
                merge.setNext(graph.add(dynamicDeopt));
            }
            List<AbstractDeoptimizeNode> obsoletes = new LinkedList<>();
            obsoletes.add(first);

            do {
                AbstractDeoptimizeNode deopt = iterator.next();
                EndNode newEnd = graph.add(new EndNode());
                merge.addForwardEnd(newEnd);
                reasonActionPhi.addInput(deopt.getActionAndReason(context.getMetaAccess()));
                speculationPhi.addInput(deopt.getSpeculation(context.getMetaAccess()));
                deopt.replaceAtPredecessor(newEnd);
                exitLoops(deopt, newEnd, cfg);
                obsoletes.add(deopt);
            } while (iterator.hasNext());

            dynamicDeopt.setStateBefore(fs);
            for (AbstractDeoptimizeNode obsolete : obsoletes) {
                obsolete.safeDelete();
            }
        }
    }

    private static void exitLoops(AbstractDeoptimizeNode deopt, EndNode end, ControlFlowGraph cfg) {
        Block block = cfg.blockFor(deopt);
        Loop<Block> loop = block.getLoop();
        while (loop != null) {
            end.graph().addBeforeFixed(end, end.graph().add(new LoopExitNode((LoopBeginNode) loop.getHeader().getBeginNode())));
            loop = loop.getParent();
        }
    }

    @Override
    public float codeSizeIncrease() {
        return 2.5f;
    }
}
