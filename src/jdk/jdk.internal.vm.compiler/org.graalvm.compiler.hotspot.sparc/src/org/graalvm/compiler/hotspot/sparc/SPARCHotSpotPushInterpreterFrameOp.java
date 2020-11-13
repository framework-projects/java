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


package org.graalvm.compiler.hotspot.sparc;

import jdk.vm.ci.code.Register;
import jdk.vm.ci.meta.AllocatableValue;
import org.graalvm.compiler.asm.sparc.SPARCMacroAssembler;
import org.graalvm.compiler.lir.LIRInstructionClass;
import org.graalvm.compiler.lir.Opcode;
import org.graalvm.compiler.lir.asm.CompilationResultBuilder;
import org.graalvm.compiler.lir.sparc.SPARCLIRInstruction;

import static jdk.vm.ci.code.ValueUtil.asRegister;
import static jdk.vm.ci.sparc.SPARC.*;
import static org.graalvm.compiler.lir.LIRInstruction.OperandFlag.REG;

/**
 * Pushes an interpreter frame to the stack.
 */
@Opcode("PUSH_INTERPRETER_FRAME")
final class SPARCHotSpotPushInterpreterFrameOp extends SPARCLIRInstruction {
    public static final LIRInstructionClass<SPARCHotSpotPushInterpreterFrameOp> TYPE = LIRInstructionClass.create(SPARCHotSpotPushInterpreterFrameOp.class);

    @Alive(REG) AllocatableValue frameSize;
    @Alive(REG) AllocatableValue framePc;
    @Alive(REG) AllocatableValue senderSp;
    @Alive(REG) AllocatableValue initialInfo;

    SPARCHotSpotPushInterpreterFrameOp(AllocatableValue frameSize, AllocatableValue framePc, AllocatableValue senderSp, AllocatableValue initialInfo) {
        super(TYPE);
        this.frameSize = frameSize;
        this.framePc = framePc;
        this.senderSp = senderSp;
        this.initialInfo = initialInfo;
    }

    @Override
    public void emitCode(CompilationResultBuilder crb, SPARCMacroAssembler masm) {
        final Register frameSizeRegister = asRegister(frameSize);
        final Register framePcRegister = asRegister(framePc);
        final Register senderSpRegister = asRegister(senderSp);

        // Save sender SP to O5_savedSP.
        masm.mov(senderSpRegister, o5);

        masm.neg(frameSizeRegister);
        masm.save(sp, frameSizeRegister, sp);

        masm.mov(i0, o0);
        masm.mov(i1, o1);
        masm.mov(i2, o2);
        masm.mov(i3, o3);
        masm.mov(i4, o4);

        // NOTE: Don't touch I5 as it contains valuable saved SP!

        // Move frame's new PC into i7
        masm.mov(framePcRegister, i7);
    }

    @Override
    public boolean leavesRegisterWindow() {
        return true;
    }
}
