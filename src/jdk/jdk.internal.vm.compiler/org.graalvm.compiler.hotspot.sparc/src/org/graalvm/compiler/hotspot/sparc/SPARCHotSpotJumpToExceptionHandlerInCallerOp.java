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


package org.graalvm.compiler.hotspot.sparc;

import jdk.vm.ci.code.Register;
import jdk.vm.ci.meta.AllocatableValue;
import org.graalvm.compiler.asm.sparc.SPARCAddress;
import org.graalvm.compiler.asm.sparc.SPARCAssembler.CC;
import org.graalvm.compiler.asm.sparc.SPARCAssembler.ConditionFlag;
import org.graalvm.compiler.asm.sparc.SPARCMacroAssembler;
import org.graalvm.compiler.asm.sparc.SPARCMacroAssembler.ScratchRegister;
import org.graalvm.compiler.lir.LIRInstructionClass;
import org.graalvm.compiler.lir.Opcode;
import org.graalvm.compiler.lir.asm.CompilationResultBuilder;

import static jdk.vm.ci.code.ValueUtil.asRegister;
import static jdk.vm.ci.sparc.SPARC.*;
import static org.graalvm.compiler.lir.LIRInstruction.OperandFlag.REG;

/**
 * Sets up the arguments for an exception handler in the callers frame, removes the current frame
 * and jumps to the handler.
 */
@Opcode("JUMP_TO_EXCEPTION_HANDLER_IN_CALLER")
final class SPARCHotSpotJumpToExceptionHandlerInCallerOp extends SPARCHotSpotEpilogueOp {

    public static final LIRInstructionClass<SPARCHotSpotJumpToExceptionHandlerInCallerOp> TYPE = LIRInstructionClass.create(SPARCHotSpotJumpToExceptionHandlerInCallerOp.class);
    public static final SizeEstimate SIZE = SizeEstimate.create(5);

    @Use(REG) AllocatableValue handlerInCallerPc;
    @Use(REG) AllocatableValue exception;
    @Use(REG) AllocatableValue exceptionPc;
    private final Register thread;
    private final int isMethodHandleReturnOffset;

    SPARCHotSpotJumpToExceptionHandlerInCallerOp(AllocatableValue handlerInCallerPc, AllocatableValue exception, AllocatableValue exceptionPc, int isMethodHandleReturnOffset, Register thread) {
        super(TYPE, SIZE);
        this.handlerInCallerPc = handlerInCallerPc;
        this.exception = exception;
        this.exceptionPc = exceptionPc;
        this.isMethodHandleReturnOffset = isMethodHandleReturnOffset;
        this.thread = thread;
    }

    @Override
    public void emitCode(CompilationResultBuilder crb, SPARCMacroAssembler masm) {
        // Restore SP from L7 if the exception PC is a method handle call site.
        SPARCAddress dst = new SPARCAddress(thread, isMethodHandleReturnOffset);
        try (ScratchRegister scratch = masm.getScratchRegister()) {
            Register scratchReg = scratch.getRegister();
            masm.lduw(dst, scratchReg);
            masm.cmp(scratchReg, scratchReg);
            masm.movcc(ConditionFlag.NotZero, CC.Icc, l7, sp);
        }
        masm.jmpl(asRegister(handlerInCallerPc), 0, g0);
        leaveFrame(crb); // Delay slot
    }
}
