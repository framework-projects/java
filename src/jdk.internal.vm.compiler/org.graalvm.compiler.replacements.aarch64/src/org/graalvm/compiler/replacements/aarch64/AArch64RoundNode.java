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



package org.graalvm.compiler.replacements.aarch64;

import jdk.vm.ci.meta.JavaConstant;
import jdk.vm.ci.meta.JavaKind;
import org.graalvm.compiler.core.common.type.FloatStamp;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.debug.GraalError;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.CanonicalizerTool;
import org.graalvm.compiler.lir.gen.ArithmeticLIRGeneratorTool;
import org.graalvm.compiler.lir.aarch64.AArch64ArithmeticLIRGeneratorTool;
import org.graalvm.compiler.lir.aarch64.AArch64ArithmeticLIRGeneratorTool.RoundingMode;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.NodeView;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.calc.UnaryNode;
import org.graalvm.compiler.nodes.spi.ArithmeticLIRLowerable;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_8;

/**
 * Round floating-point value.
 */
@NodeInfo(cycles = CYCLES_8)
public final class AArch64RoundNode extends UnaryNode implements ArithmeticLIRLowerable {
    public static final NodeClass<AArch64RoundNode> TYPE = NodeClass.create(AArch64RoundNode.class);

    private final RoundingMode mode;

    public AArch64RoundNode(ValueNode value, RoundingMode mode) {
        super(TYPE, roundStamp((FloatStamp) value.stamp(NodeView.DEFAULT), mode), value);
        this.mode = mode;
    }

    private static double round(RoundingMode mode, double input) {
        switch (mode) {
            case DOWN:
                return Math.floor(input);
            case NEAREST:
                return Math.rint(input);
            case UP:
                return Math.ceil(input);
            case TRUNCATE:
                return (long) input;
            default:
                throw GraalError.unimplemented("unimplemented RoundingMode " + mode);
        }
    }

    private static FloatStamp roundStamp(FloatStamp stamp, RoundingMode mode) {
        double min = stamp.lowerBound();
        min = Math.min(min, round(mode, min));

        double max = stamp.upperBound();
        max = Math.max(max, round(mode, max));

        return new FloatStamp(stamp.getBits(), min, max, stamp.isNonNaN());
    }

    @Override
    public Stamp foldStamp(Stamp newStamp) {
        assert newStamp.isCompatible(getValue().stamp(NodeView.DEFAULT));
        return roundStamp((FloatStamp) newStamp, mode);
    }

    private ValueNode tryFold(ValueNode input) {
        if (input.isConstant()) {
            JavaConstant c = input.asJavaConstant();
            if (c.getJavaKind() == JavaKind.Double) {
                return ConstantNode.forDouble(round(mode, c.asDouble()));
            } else if (c.getJavaKind() == JavaKind.Float) {
                return ConstantNode.forFloat((float) round(mode, c.asFloat()));
            }
        }
        return null;
    }

    @Override
    public ValueNode canonical(CanonicalizerTool tool, ValueNode forValue) {
        ValueNode folded = tryFold(forValue);
        return folded != null ? folded : this;
    }

    @Override
    public void generate(NodeLIRBuilderTool builder, ArithmeticLIRGeneratorTool gen) {
        builder.setResult(this, ((AArch64ArithmeticLIRGeneratorTool) gen).emitRound(builder.operand(getValue()), mode));
    }
}
