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


package org.graalvm.compiler.lir.alloc.lsra;

import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.lir.gen.LIRGenerationResult;
import static org.graalvm.compiler.lir.phases.AllocationPhase.AllocationContext;
import org.graalvm.compiler.lir.phases.LIRPhase;

import jdk.vm.ci.code.TargetDescription;

abstract class LinearScanAllocationPhase {

    final CharSequence getName() {
        return LIRPhase.createName(getClass());
    }

    @Override
    public final String toString() {
        return getName().toString();
    }

    public final void apply(TargetDescription target, LIRGenerationResult lirGenRes, AllocationContext context) {
        apply(target, lirGenRes, context, true);
    }

    @SuppressWarnings("try")
    public final void apply(TargetDescription target, LIRGenerationResult lirGenRes, AllocationContext context, boolean dumpLIR) {
        DebugContext debug = lirGenRes.getLIR().getDebug();
        try (DebugContext.Scope s = debug.scope(getName(), this)) {
            run(target, lirGenRes, context);
            if (dumpLIR) {
                if (debug.isDumpEnabled(DebugContext.VERBOSE_LEVEL)) {
                    debug.dump(DebugContext.VERBOSE_LEVEL, lirGenRes.getLIR(), "After %s", getName());
                }
            }
        } catch (Throwable e) {
            throw debug.handle(e);
        }
    }

    protected abstract void run(TargetDescription target, LIRGenerationResult lirGenRes, AllocationContext context);

}
