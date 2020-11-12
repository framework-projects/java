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


package org.graalvm.compiler.replacements.nodes.arithmetic;

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_2;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_2;

import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable.BinaryOp.UMulHigh;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.Canonicalizable;
import org.graalvm.compiler.graph.spi.CanonicalizerTool;
import org.graalvm.compiler.lir.gen.ArithmeticLIRGeneratorTool;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.NodeView;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.calc.BinaryArithmeticNode;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;

import jdk.vm.ci.meta.Constant;
import jdk.vm.ci.meta.PrimitiveConstant;
import jdk.vm.ci.meta.Value;

@NodeInfo(shortName = "|*H|", cycles = CYCLES_2, size = SIZE_2)
public final class UnsignedMulHighNode extends BinaryArithmeticNode<UMulHigh> implements Canonicalizable.BinaryCommutative<ValueNode> {
    public static final NodeClass<UnsignedMulHighNode> TYPE = NodeClass.create(UnsignedMulHighNode.class);

    public UnsignedMulHighNode(ValueNode x, ValueNode y) {
        super(TYPE, ArithmeticOpTable::getUMulHigh, x, y);
    }

    @Override
    public void generate(NodeLIRBuilderTool nodeValueMap, ArithmeticLIRGeneratorTool gen) {
        Value a = nodeValueMap.operand(getX());
        Value b = nodeValueMap.operand(getY());
        nodeValueMap.setResult(this, gen.emitUMulHigh(a, b));
    }

    @Override
    public ValueNode canonical(CanonicalizerTool tool, ValueNode forX, ValueNode forY) {
        ValueNode ret = super.canonical(tool, forX, forY);
        if (ret != this) {
            return ret;
        }

        if (forX.isConstant() && !forY.isConstant()) {
            // we try to swap and canonicalize
            ValueNode improvement = canonical(tool, forY, forX);
            if (improvement != this) {
                return improvement;
            }
            // if this fails we only swap
            return new UnsignedMulHighNode(forY, forX);
        }
        return canonical(this, forY);
    }

    private static ValueNode canonical(UnsignedMulHighNode self, ValueNode forY) {
        if (forY.isConstant()) {
            Constant c = forY.asConstant();
            if (c instanceof PrimitiveConstant && ((PrimitiveConstant) c).getJavaKind().isNumericInteger()) {
                long i = ((PrimitiveConstant) c).asLong();
                if (i == 0 || i == 1) {
                    return ConstantNode.forIntegerStamp(self.stamp(NodeView.DEFAULT), 0);
                }
            }
        }
        return self;
    }
}
