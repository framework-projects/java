/*
 * Copyright (c) 2014, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.lir;

import jdk.vm.ci.meta.Value;
import org.graalvm.compiler.lir.LIRInstruction.OperandFlag;
import org.graalvm.compiler.lir.LIRInstruction.OperandMode;

import java.util.EnumSet;

/**
 * Non-modifying version of {@link ValueProcedure}.
 */
@FunctionalInterface
public interface ValueConsumer extends InstructionValueConsumer {

    /**
     * Iterator method to be overwritten.
     *
     * @param value The value that is iterated.
     * @param mode The operand mode for the value.
     * @param flags A set of flags for the value.
     */
    void visitValue(Value value, OperandMode mode, EnumSet<OperandFlag> flags);

    @Override
    default void visitValue(LIRInstruction instruction, Value value, OperandMode mode, EnumSet<OperandFlag> flags) {
        visitValue(value, mode, flags);
    }
}
