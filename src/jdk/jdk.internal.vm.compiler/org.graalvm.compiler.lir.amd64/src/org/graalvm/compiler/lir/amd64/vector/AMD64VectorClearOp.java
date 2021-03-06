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


package org.graalvm.compiler.lir.amd64.vector;

import jdk.vm.ci.amd64.AMD64Kind;
import jdk.vm.ci.code.Register;
import jdk.vm.ci.meta.AllocatableValue;
import org.graalvm.compiler.asm.amd64.AMD64MacroAssembler;
import org.graalvm.compiler.asm.amd64.AVXKind;
import org.graalvm.compiler.lir.LIRInstruction;
import org.graalvm.compiler.lir.LIRInstructionClass;
import org.graalvm.compiler.lir.amd64.AMD64LIRInstruction;
import org.graalvm.compiler.lir.asm.CompilationResultBuilder;

import static jdk.vm.ci.code.ValueUtil.asRegister;
import static org.graalvm.compiler.asm.amd64.AMD64Assembler.VexRVMOp.*;
import static org.graalvm.compiler.asm.amd64.AVXKind.AVXSize.XMM;
import static org.graalvm.compiler.lir.LIRInstruction.OperandFlag.REG;

public class AMD64VectorClearOp extends AMD64LIRInstruction {
    public static final LIRInstructionClass<AMD64VectorClearOp> TYPE = LIRInstructionClass.create(AMD64VectorClearOp.class);

    protected @LIRInstruction.Def({REG}) AllocatableValue result;

    public AMD64VectorClearOp(AllocatableValue result) {
        this(TYPE, result);
    }

    protected AMD64VectorClearOp(LIRInstructionClass<? extends AMD64VectorClearOp> c, AllocatableValue result) {
        super(c);
        this.result = result;
    }

    @Override
    public void emitCode(CompilationResultBuilder crb, AMD64MacroAssembler masm) {
        AMD64Kind kind = (AMD64Kind) result.getPlatformKind();
        Register register = asRegister(result);

        switch (kind.getScalar()) {
            case SINGLE:
                VXORPS.emit(masm, AVXKind.getRegisterSize(kind), register, register, register);
                break;

            case DOUBLE:
                VXORPD.emit(masm, AVXKind.getRegisterSize(kind), register, register, register);
                break;

            default:
                // on AVX1, YMM VPXOR is not supported - still it is possible to clear the whole YMM
                // register as the upper 128-bit are implicitly cleared by the AVX1 instruction.
                VPXOR.emit(masm, XMM, register, register, register);
        }
    }
}
