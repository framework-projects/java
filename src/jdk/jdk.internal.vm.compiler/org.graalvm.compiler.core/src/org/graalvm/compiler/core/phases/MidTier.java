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


package org.graalvm.compiler.core.phases;

import org.graalvm.compiler.loop.DefaultLoopPolicies;
import org.graalvm.compiler.loop.LoopPolicies;
import org.graalvm.compiler.loop.phases.LoopPartialUnrollPhase;
import org.graalvm.compiler.loop.phases.LoopSafepointEliminationPhase;
import org.graalvm.compiler.loop.phases.ReassociateInvariantPhase;
import org.graalvm.compiler.nodes.spi.LoweringTool;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.phases.PhaseSuite;
import org.graalvm.compiler.phases.common.*;
import org.graalvm.compiler.phases.tiers.MidTierContext;

import static org.graalvm.compiler.core.common.GraalOptions.*;
import static org.graalvm.compiler.core.common.SpeculativeExecutionAttacksMitigations.GuardTargets;
import static org.graalvm.compiler.core.common.SpeculativeExecutionAttacksMitigations.NonDeoptGuardTargets;
import static org.graalvm.compiler.core.common.SpeculativeExecutionAttacksMitigations.Options.MitigateSpeculativeExecutionAttacks;

public class MidTier extends PhaseSuite<MidTierContext> {

    public MidTier(OptionValues options) {
        CanonicalizerPhase canonicalizer = new CanonicalizerPhase();
        if (ImmutableCode.getValue(options)) {
            canonicalizer.disableReadCanonicalization();
        }

        appendPhase(new LockEliminationPhase());

        if (OptFloatingReads.getValue(options)) {
            appendPhase(new IncrementalCanonicalizerPhase<>(canonicalizer, new FloatingReadPhase()));
        }

        if (ConditionalElimination.getValue(options)) {
            appendPhase(new IterativeConditionalEliminationPhase(canonicalizer, true));
        }

        appendPhase(new LoopSafepointEliminationPhase());

        appendPhase(new LoopSafepointInsertionPhase());

        appendPhase(new GuardLoweringPhase());

        if (MitigateSpeculativeExecutionAttacks.getValue(options) == GuardTargets || MitigateSpeculativeExecutionAttacks.getValue(options) == NonDeoptGuardTargets) {
            appendPhase(new InsertGuardFencesPhase());
        }

        if (VerifyHeapAtReturn.getValue(options)) {
            appendPhase(new VerifyHeapAtReturnPhase());
        }

        appendPhase(new LoweringPhase(canonicalizer, LoweringTool.StandardLoweringStage.MID_TIER));

        appendPhase(new FrameStateAssignmentPhase());

        LoopPolicies loopPolicies = createLoopPolicies();
        if (OptLoopTransform.getValue(options)) {
            if (PartialUnroll.getValue(options)) {
                appendPhase(new LoopPartialUnrollPhase(loopPolicies, canonicalizer));
            }
        }
        if (ReassociateInvariants.getValue(options)) {
            appendPhase(new ReassociateInvariantPhase());
        }

        if (OptDeoptimizationGrouping.getValue(options)) {
            appendPhase(new DeoptimizationGroupingPhase());
        }

        appendPhase(canonicalizer);

        appendPhase(new WriteBarrierAdditionPhase());
    }

    public LoopPolicies createLoopPolicies() {
        return new DefaultLoopPolicies();
    }
}
