/*
 * Copyright (c) 2013, 2019, Oracle and/or its affiliates. All rights reserved.
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

import org.graalvm.compiler.debug.DebugCloseable;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.gc.BarrierSet;
import org.graalvm.compiler.nodes.memory.FixedAccessNode;
import org.graalvm.compiler.phases.BasePhase;
import org.graalvm.compiler.phases.tiers.MidTierContext;

public class WriteBarrierAdditionPhase extends BasePhase<MidTierContext> {
    @SuppressWarnings("try")
    @Override
    protected void run(StructuredGraph graph, MidTierContext context) {
        BarrierSet barrierSet = context.getGC().getBarrierSet();
        for (FixedAccessNode n : graph.getNodes().filter(FixedAccessNode.class)) {
            try (DebugCloseable scope = n.graph().withNodeSourcePosition(n)) {
                barrierSet.addBarriers(n);
            }
        }
    }

    @Override
    public boolean checkContract() {
        return false;
    }
}
