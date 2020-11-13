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
 * Functional interface for iterating over a list of values without modifying them. See
 * {@link InstructionValueProcedure} for a version that can modify values.
 */
@FunctionalInterface
public interface InstructionValueConsumer {

    /**
     * Iterator method to be overwritten.
     *
     * @param instruction The current instruction.
     * @param value The value that is iterated.
     * @param mode The operand mode for the value.
     * @param flags A set of flags for the value.
     */
    void visitValue(LIRInstruction instruction, Value value, OperandMode mode, EnumSet<OperandFlag> flags);
}
