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


package org.graalvm.compiler.hotspot.aarch64;

import jdk.vm.ci.code.CallingConvention;
import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.RegisterValue;
import org.graalvm.compiler.asm.Label;
import org.graalvm.compiler.asm.aarch64.AArch64Address;
import org.graalvm.compiler.asm.aarch64.AArch64Assembler;
import org.graalvm.compiler.asm.aarch64.AArch64MacroAssembler;
import org.graalvm.compiler.asm.aarch64.AArch64MacroAssembler.ScratchRegister;
import org.graalvm.compiler.core.common.spi.ForeignCallLinkage;
import org.graalvm.compiler.hotspot.GraalHotSpotVMConfig;
import org.graalvm.compiler.hotspot.meta.HotSpotForeignCallsProvider;
import org.graalvm.compiler.lir.LIRInstructionClass;
import org.graalvm.compiler.lir.aarch64.AArch64BlockEndOp;
import org.graalvm.compiler.lir.aarch64.AArch64Call;
import org.graalvm.compiler.lir.asm.CompilationResultBuilder;

import static jdk.vm.ci.aarch64.AArch64.*;
import static jdk.vm.ci.hotspot.aarch64.AArch64HotSpotRegisterConfig.fp;
import static org.graalvm.compiler.hotspot.HotSpotHostBackend.ENABLE_STACK_RESERVED_ZONE;
import static org.graalvm.compiler.hotspot.HotSpotHostBackend.THROW_DELAYED_STACKOVERFLOW_ERROR;

/**
 * Superclass for operations that leave a method's frame.
 */
abstract class AArch64HotSpotEpilogueOp extends AArch64BlockEndOp {

    private final GraalHotSpotVMConfig config;
    private final Register thread;

    protected AArch64HotSpotEpilogueOp(LIRInstructionClass<? extends AArch64HotSpotEpilogueOp> c, GraalHotSpotVMConfig config, Register thread) {
        super(c);
        this.config = config;
        this.thread = thread;
    }

    protected AArch64HotSpotEpilogueOp(LIRInstructionClass<? extends AArch64HotSpotEpilogueOp> c, GraalHotSpotVMConfig config) {
        super(c);
        this.config = config;
        this.thread = null; // no safepoint
    }

    protected void leaveFrame(CompilationResultBuilder crb, AArch64MacroAssembler masm, boolean emitSafepoint, boolean requiresReservedStackAccessCheck) {
        assert crb.frameContext != null : "We never elide frames in aarch64";
        crb.frameContext.leave(crb);
        if (requiresReservedStackAccessCheck) {
            HotSpotForeignCallsProvider foreignCalls = (HotSpotForeignCallsProvider) crb.foreignCalls;
            Label noReserved = new Label();
            try (ScratchRegister sc = masm.getScratchRegister()) {
                Register scratch = sc.getRegister();
                masm.ldr(64, scratch, masm.makeAddress(thread, config.javaThreadReservedStackActivationOffset, 8));
                masm.subs(64, zr, sp, scratch);
            }
            masm.branchConditionally(AArch64Assembler.ConditionFlag.LO, noReserved);
            ForeignCallLinkage enableStackReservedZone = foreignCalls.lookupForeignCall(ENABLE_STACK_RESERVED_ZONE);
            CallingConvention cc = enableStackReservedZone.getOutgoingCallingConvention();
            assert cc.getArgumentCount() == 1;
            Register arg0 = ((RegisterValue) cc.getArgument(0)).getRegister();
            masm.mov(64, arg0, thread);
            try (ScratchRegister sc = masm.getScratchRegister()) {
                masm.stp(64, fp, lr, AArch64Address.createPreIndexedImmediateAddress(sp, -2));
                AArch64Call.directCall(crb, masm, enableStackReservedZone, sc.getRegister(), null);
                masm.ldp(64, fp, lr, AArch64Address.createPostIndexedImmediateAddress(sp, 2));
            }
            AArch64Call.directJmp(crb, masm, foreignCalls.lookupForeignCall(THROW_DELAYED_STACKOVERFLOW_ERROR));
            masm.bind(noReserved);
        }
        if (emitSafepoint) {
            try (ScratchRegister sc = masm.getScratchRegister()) {
                Register scratch = sc.getRegister();
                AArch64HotSpotSafepointOp.emitCode(crb, masm, config, true, thread, scratch, null);
            }
        }
    }
}
