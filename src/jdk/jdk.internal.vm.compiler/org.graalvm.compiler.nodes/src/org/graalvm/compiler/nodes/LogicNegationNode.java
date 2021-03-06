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


package org.graalvm.compiler.nodes;

import jdk.vm.ci.meta.TriState;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.Canonicalizable;
import org.graalvm.compiler.graph.spi.CanonicalizerTool;
import org.graalvm.compiler.nodeinfo.NodeInfo;

import static org.graalvm.compiler.nodeinfo.InputType.Condition;
import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_0;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_0;

/**
 * Logic node that negates its argument.
 */
@NodeInfo(cycles = CYCLES_0, size = SIZE_0)
public final class LogicNegationNode extends LogicNode implements Canonicalizable.Unary<LogicNode> {

    public static final NodeClass<LogicNegationNode> TYPE = NodeClass.create(LogicNegationNode.class);
    @Input(Condition) LogicNode value;

    public LogicNegationNode(LogicNode value) {
        super(TYPE);
        this.value = value;
    }

    public static LogicNode create(LogicNode value) {
        LogicNode synonym = findSynonym(value);
        if (synonym != null) {
            return synonym;
        }
        return new LogicNegationNode(value);
    }

    private static LogicNode findSynonym(LogicNode value) {
        if (value instanceof LogicConstantNode) {
            LogicConstantNode logicConstantNode = (LogicConstantNode) value;
            return LogicConstantNode.forBoolean(!logicConstantNode.getValue());
        } else if (value instanceof LogicNegationNode) {
            return ((LogicNegationNode) value).getValue();
        }
        return null;
    }

    @Override
    public LogicNode getValue() {
        return value;
    }

    @Override
    public LogicNode canonical(CanonicalizerTool tool, LogicNode forValue) {
        LogicNode synonym = findSynonym(forValue);
        if (synonym != null) {
            return synonym;
        }
        return this;
    }

    @Override
    public TriState implies(boolean thisNegated, LogicNode other) {
        if (other == getValue()) {
            return TriState.get(thisNegated);
        }
        return getValue().implies(!thisNegated, other);
    }
}
