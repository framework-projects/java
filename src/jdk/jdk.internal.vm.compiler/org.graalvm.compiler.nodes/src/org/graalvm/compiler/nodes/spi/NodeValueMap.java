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



package org.graalvm.compiler.nodes.spi;

import jdk.vm.ci.meta.Value;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.nodes.ValueNode;

public interface NodeValueMap {

    /**
     * Returns the operand that has been previously initialized by
     * {@link #setResult(ValueNode, Value)} with the result of an instruction. It's a code
     * generation error to ask for the operand of ValueNode that doesn't have one yet.
     *
     * @param node A node that produces a result value.
     */
    Value operand(Node node);

    /**
     * @return {@code true} if there is an {@link Value operand} associated with the {@code node} in
     *         the current block.
     */
    boolean hasOperand(Node node);

    /**
     * Associates {@code operand} with the {@code node} in the current block.
     *
     * @return {@code operand}
     */
    Value setResult(ValueNode node, Value operand);

    /**
     * Gets the {@link ValueNode} that produced a {@code value}. If the {@code value} is not
     * associated with a {@link ValueNode} {@code null} is returned.
     *
     * This method is intended for debugging purposes only.
     */
    ValueNode valueForOperand(Value value);
}
