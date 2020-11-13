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


package org.graalvm.compiler.core.phases;

import org.graalvm.compiler.nodes.spi.LoweringTool;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.phases.PhaseSuite;
import org.graalvm.compiler.phases.common.*;
import org.graalvm.compiler.phases.tiers.MidTierContext;

import static org.graalvm.compiler.core.common.GraalOptions.ImmutableCode;

public class EconomyMidTier extends PhaseSuite<MidTierContext> {

    public EconomyMidTier(OptionValues options) {
        CanonicalizerPhase canonicalizer = new CanonicalizerPhase();
        if (ImmutableCode.getValue(options)) {
            canonicalizer.disableReadCanonicalization();
        }
        appendPhase(new RemoveValueProxyPhase());

        appendPhase(new LoopSafepointInsertionPhase());

        appendPhase(new GuardLoweringPhase());

        appendPhase(new LoweringPhase(canonicalizer, LoweringTool.StandardLoweringStage.MID_TIER));

        appendPhase(new FrameStateAssignmentPhase());

        appendPhase(new WriteBarrierAdditionPhase());
    }
}
