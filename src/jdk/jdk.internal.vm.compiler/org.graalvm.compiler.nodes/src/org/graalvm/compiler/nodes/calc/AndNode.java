/*
 * Copyright (c) 2011, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.nodes.calc;

import jdk.vm.ci.code.CodeUtil;
import jdk.vm.ci.meta.Constant;
import jdk.vm.ci.meta.PrimitiveConstant;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable.BinaryOp;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable.BinaryOp.And;
import org.graalvm.compiler.core.common.type.IntegerStamp;
import org.graalvm.compiler.core.common.type.PrimitiveStamp;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.Canonicalizable.BinaryCommutative;
import org.graalvm.compiler.graph.spi.CanonicalizerTool;
import org.graalvm.compiler.lir.gen.ArithmeticLIRGeneratorTool;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.NodeView;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;
import org.graalvm.compiler.nodes.util.GraphUtil;

@NodeInfo(shortName = "&")
public final class AndNode extends BinaryArithmeticNode<And> implements NarrowableArithmeticNode, BinaryCommutative<ValueNode> {

    public static final NodeClass<AndNode> TYPE = NodeClass.create(AndNode.class);

    public AndNode(ValueNode x, ValueNode y) {
        super(TYPE, ArithmeticOpTable::getAnd, x, y);
    }

    public static ValueNode create(ValueNode x, ValueNode y, NodeView view) {
        BinaryOp<And> op = ArithmeticOpTable.forStamp(x.stamp(view)).getAnd();
        Stamp stamp = op.foldStamp(x.stamp(view), y.stamp(view));
        ConstantNode tryConstantFold = tryConstantFold(op, x, y, stamp, view);
        if (tryConstantFold != null) {
            return tryConstantFold;
        }
        return canonical(null, op, stamp, x, y, view);
    }

    @Override
    public ValueNode canonical(CanonicalizerTool tool, ValueNode forX, ValueNode forY) {
        ValueNode ret = super.canonical(tool, forX, forY);
        if (ret != this) {
            return ret;
        }

        NodeView view = NodeView.from(tool);
        return canonical(this, getOp(forX, forY), stamp(view), forX, forY, view);
    }

    private static ValueNode canonical(AndNode self, BinaryOp<And> op, Stamp stamp, ValueNode forX, ValueNode forY, NodeView view) {
        if (GraphUtil.unproxify(forX) == GraphUtil.unproxify(forY)) {
            return forX;
        }
        if (forX.isConstant() && !forY.isConstant()) {
            return new AndNode(forY, forX);
        }
        if (forY.isConstant()) {
            Constant c = forY.asConstant();
            if (op.isNeutral(c)) {
                return forX;
            }

            if (c instanceof PrimitiveConstant && ((PrimitiveConstant) c).getJavaKind().isNumericInteger()) {
                long rawY = ((PrimitiveConstant) c).asLong();
                long mask = CodeUtil.mask(PrimitiveStamp.getBits(stamp));
                if ((rawY & mask) == 0) {
                    return ConstantNode.forIntegerStamp(stamp, 0);
                }
                if (forX instanceof SignExtendNode) {
                    SignExtendNode ext = (SignExtendNode) forX;
                    if (rawY == ((1L << ext.getInputBits()) - 1)) {
                        return new ZeroExtendNode(ext.getValue(), ext.getResultBits());
                    }
                }
                IntegerStamp xStamp = (IntegerStamp) forX.stamp(view);
                if (((xStamp.upMask() | xStamp.downMask()) & ~rawY) == 0) {
                    // No bits are set which are outside the mask, so the mask will have no effect.
                    return forX;
                }
            }

            return reassociate(self != null ? self : (AndNode) new AndNode(forX, forY).maybeCommuteInputs(), ValueNode.isConstantPredicate(), forX, forY, view);
        }
        if (forX instanceof NotNode && forY instanceof NotNode) {
            return new NotNode(OrNode.create(((NotNode) forX).getValue(), ((NotNode) forY).getValue(), view));
        }
        return self != null ? self : new AndNode(forX, forY).maybeCommuteInputs();
    }

    @Override
    public void generate(NodeLIRBuilderTool nodeValueMap, ArithmeticLIRGeneratorTool gen) {
        nodeValueMap.setResult(this, gen.emitAnd(nodeValueMap.operand(getX()), nodeValueMap.operand(getY())));
    }
}
