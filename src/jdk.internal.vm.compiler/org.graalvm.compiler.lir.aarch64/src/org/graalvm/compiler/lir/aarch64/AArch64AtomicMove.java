/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.lir.aarch64;

import static jdk.vm.ci.code.ValueUtil.asRegister;
import static org.graalvm.compiler.lir.LIRInstruction.OperandFlag.CONST;
import static org.graalvm.compiler.lir.LIRInstruction.OperandFlag.REG;

import org.graalvm.compiler.asm.Label;
import org.graalvm.compiler.asm.aarch64.AArch64Assembler;
import org.graalvm.compiler.asm.aarch64.AArch64MacroAssembler;
import org.graalvm.compiler.asm.aarch64.AArch64MacroAssembler.ScratchRegister;
import org.graalvm.compiler.lir.LIRInstructionClass;
import org.graalvm.compiler.lir.LIRValueUtil;
import org.graalvm.compiler.lir.Opcode;
import org.graalvm.compiler.lir.asm.CompilationResultBuilder;

import jdk.vm.ci.aarch64.AArch64Kind;
import jdk.vm.ci.code.Register;
import jdk.vm.ci.meta.AllocatableValue;
import jdk.vm.ci.meta.Value;

public class AArch64AtomicMove {
    /**
     * Compare and swap instruction. Does the following atomically: <code>
     *  CAS(newVal, expected, address):
     *    oldVal = *address
     *    if oldVal == expected:
     *        *address = newVal
     *    return oldVal
     * </code>
     */
    @Opcode("CAS")
    public static class CompareAndSwapOp extends AArch64LIRInstruction {
        public static final LIRInstructionClass<CompareAndSwapOp> TYPE = LIRInstructionClass.create(CompareAndSwapOp.class);

        @Def protected AllocatableValue resultValue;
        @Alive protected Value expectedValue;
        @Alive protected AllocatableValue newValue;
        @Alive protected AllocatableValue addressValue;
        @Temp protected AllocatableValue scratchValue;

        public CompareAndSwapOp(AllocatableValue result, Value expectedValue, AllocatableValue newValue, AllocatableValue addressValue, AllocatableValue scratch) {
            super(TYPE);
            this.resultValue = result;
            this.expectedValue = expectedValue;
            this.newValue = newValue;
            this.addressValue = addressValue;
            this.scratchValue = scratch;
        }

        @Override
        public void emitCode(CompilationResultBuilder crb, AArch64MacroAssembler masm) {
            AArch64Kind kind = (AArch64Kind) expectedValue.getPlatformKind();
            assert kind.isInteger();
            final int size = kind.getSizeInBytes() * Byte.SIZE;

            Register address = asRegister(addressValue);
            Register result = asRegister(resultValue);
            Register newVal = asRegister(newValue);
            if (AArch64LIRFlagsVersioned.useLSE(masm.target.arch)) {
                Register expected = asRegister(expectedValue);
                masm.mov(size, result, expected);
                masm.cas(size, result, newVal, address, true /* acquire */, true /* release */);
                AArch64Compare.gpCompare(masm, resultValue, expectedValue);
            } else {
                // We could avoid using a scratch register here, by reusing resultValue for the
                // stlxr success flag and issue a mov resultValue, expectedValue in case of success
                // before returning.
                Register scratch = asRegister(scratchValue);
                Label retry = new Label();
                Label fail = new Label();
                masm.bind(retry);
                masm.ldaxr(size, result, address);
                AArch64Compare.gpCompare(masm, resultValue, expectedValue);
                masm.branchConditionally(AArch64Assembler.ConditionFlag.NE, fail);
                masm.stlxr(size, scratch, newVal, address);
                // if scratch == 0 then write successful, else retry.
                masm.cbnz(32, scratch, retry);
                masm.bind(fail);
            }
        }
    }

    /**
     * Load (Read) and Add instruction. Does the following atomically: <code>
     *  ATOMIC_READ_AND_ADD(addend, result, address):
     *    result = *address
     *    *address = result + addend
     *    return result
     * </code>
     */
    @Opcode("ATOMIC_READ_AND_ADD")
    public static final class AtomicReadAndAddOp extends AArch64LIRInstruction {
        public static final LIRInstructionClass<AtomicReadAndAddOp> TYPE = LIRInstructionClass.create(AtomicReadAndAddOp.class);

        private final AArch64Kind accessKind;

        @Def({REG}) protected AllocatableValue resultValue;
        @Alive({REG}) protected AllocatableValue addressValue;
        @Alive({REG, CONST}) protected Value deltaValue;

        public AtomicReadAndAddOp(AArch64Kind kind, AllocatableValue result, AllocatableValue address, Value delta) {
            super(TYPE);
            this.accessKind = kind;
            this.resultValue = result;
            this.addressValue = address;
            this.deltaValue = delta;
        }

        @Override
        public void emitCode(CompilationResultBuilder crb, AArch64MacroAssembler masm) {
            assert accessKind.isInteger();
            final int size = accessKind.getSizeInBytes() * Byte.SIZE;

            Register address = asRegister(addressValue);
            Register result = asRegister(resultValue);

            Label retry = new Label();
            masm.bind(retry);
            masm.ldaxr(size, result, address);
            try (ScratchRegister scratchRegister1 = masm.getScratchRegister()) {
                Register scratch1 = scratchRegister1.getRegister();
                if (LIRValueUtil.isConstantValue(deltaValue)) {
                    long delta = LIRValueUtil.asConstantValue(deltaValue).getJavaConstant().asLong();
                    masm.add(size, scratch1, result, delta);
                } else { // must be a register then
                    masm.add(size, scratch1, result, asRegister(deltaValue));
                }
                try (ScratchRegister scratchRegister2 = masm.getScratchRegister()) {
                    Register scratch2 = scratchRegister2.getRegister();
                    masm.stlxr(size, scratch2, scratch1, address);
                    // if scratch2 == 0 then write successful, else retry
                    masm.cbnz(32, scratch2, retry);
                }
            }
        }
    }

    /**
     * Load (Read) and Add instruction. Does the following atomically: <code>
     *  ATOMIC_READ_AND_ADD(addend, result, address):
     *    result = *address
     *    *address = result + addend
     *    return result
     * </code>
     *
     * The LSE version has different properties with regards to the register allocator. To define
     * these differences, we have to create a separate LIR instruction class.
     *
     * The difference to {@linkplain AtomicReadAndAddOp} is:
     * <li>{@linkplain #deltaValue} must be a register (@Use({REG}) instead @Alive({REG,CONST}))
     * <li>{@linkplain #resultValue} may be an alias for the input registers (@Use instead
     * of @Alive)
     */
    @Opcode("ATOMIC_READ_AND_ADD")
    public static final class AtomicReadAndAddLSEOp extends AArch64LIRInstruction {
        public static final LIRInstructionClass<AtomicReadAndAddLSEOp> TYPE = LIRInstructionClass.create(AtomicReadAndAddLSEOp.class);

        private final AArch64Kind accessKind;

        @Def({REG}) protected AllocatableValue resultValue;
        @Use({REG}) protected AllocatableValue addressValue;
        @Use({REG}) protected AllocatableValue deltaValue;

        public AtomicReadAndAddLSEOp(AArch64Kind kind, AllocatableValue result, AllocatableValue address, AllocatableValue delta) {
            super(TYPE);
            this.accessKind = kind;
            this.resultValue = result;
            this.addressValue = address;
            this.deltaValue = delta;
        }

        @Override
        public void emitCode(CompilationResultBuilder crb, AArch64MacroAssembler masm) {
            assert accessKind.isInteger();
            final int size = accessKind.getSizeInBytes() * Byte.SIZE;

            Register address = asRegister(addressValue);
            Register delta = asRegister(deltaValue);
            Register result = asRegister(resultValue);
            masm.ldadd(size, delta, result, address, true, true);
        }
    }

    /**
     * Load (Read) and Write instruction. Does the following atomically: <code>
     *  ATOMIC_READ_AND_WRITE(newValue, result, address):
     *    result = *address
     *    *address = newValue
     *    return result
     * </code>
     */
    @Opcode("ATOMIC_READ_AND_WRITE")
    public static final class AtomicReadAndWriteOp extends AArch64LIRInstruction {
        public static final LIRInstructionClass<AtomicReadAndWriteOp> TYPE = LIRInstructionClass.create(AtomicReadAndWriteOp.class);

        private final AArch64Kind accessKind;

        @Def protected AllocatableValue resultValue;
        @Alive protected AllocatableValue addressValue;
        @Alive protected AllocatableValue newValue;
        @Temp protected AllocatableValue scratchValue;

        public AtomicReadAndWriteOp(AArch64Kind kind, AllocatableValue result, AllocatableValue address, AllocatableValue newValue, AllocatableValue scratch) {
            super(TYPE);
            this.accessKind = kind;
            this.resultValue = result;
            this.addressValue = address;
            this.newValue = newValue;
            this.scratchValue = scratch;
        }

        @Override
        public void emitCode(CompilationResultBuilder crb, AArch64MacroAssembler masm) {
            assert accessKind.isInteger();
            final int size = accessKind.getSizeInBytes() * Byte.SIZE;

            Register address = asRegister(addressValue);
            Register value = asRegister(newValue);
            Register result = asRegister(resultValue);

            if (AArch64LIRFlagsVersioned.useLSE(masm.target.arch)) {
                masm.swp(size, value, result, address, true, true);
            } else {
                Register scratch = asRegister(scratchValue);
                Label retry = new Label();
                masm.bind(retry);
                masm.ldaxr(size, result, address);
                masm.stlxr(size, scratch, value, address);
                // if scratch == 0 then write successful, else retry
                masm.cbnz(32, scratch, retry);
            }
        }
    }
}
