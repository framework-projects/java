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


package org.graalvm.compiler.loop;

import org.graalvm.compiler.core.common.type.IntegerStamp;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.core.common.util.UnsignedLong;
import org.graalvm.compiler.debug.GraalError;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.calc.*;

import static org.graalvm.compiler.loop.MathUtil.*;

public class BasicInductionVariable extends InductionVariable {

    private final ValuePhiNode phi;
    private final ValueNode init;
    private ValueNode rawStride;
    private BinaryArithmeticNode<?> op;

    public BasicInductionVariable(LoopEx loop, ValuePhiNode phi, ValueNode init, ValueNode rawStride, BinaryArithmeticNode<?> op) {
        super(loop);
        this.phi = phi;
        this.init = init;
        this.rawStride = rawStride;
        this.op = op;
    }

    @Override
    public StructuredGraph graph() {
        return phi.graph();
    }

    public BinaryArithmeticNode<?> getOp() {
        return op;
    }

    public void setOP(BinaryArithmeticNode<?> newOp) {
        rawStride = newOp.getY();
        op = newOp;
    }

    @Override
    public Direction direction() {
        Stamp stamp = rawStride.stamp(NodeView.DEFAULT);
        if (stamp instanceof IntegerStamp) {
            IntegerStamp integerStamp = (IntegerStamp) stamp;
            Direction dir = null;
            if (integerStamp.isStrictlyPositive()) {
                dir = Direction.Up;
            } else if (integerStamp.isStrictlyNegative()) {
                dir = Direction.Down;
            }
            if (dir != null) {
                if (op instanceof AddNode) {
                    return dir;
                } else {
                    assert op instanceof SubNode;
                    return dir.opposite();
                }
            }
        }
        return null;
    }

    @Override
    public ValuePhiNode valueNode() {
        return phi;
    }

    @Override
    public ValueNode initNode() {
        return init;
    }

    @Override
    public ValueNode strideNode() {
        if (op instanceof AddNode) {
            return rawStride;
        }
        if (op instanceof SubNode) {
            return graph().unique(new NegateNode(rawStride));
        }
        throw GraalError.shouldNotReachHere();
    }

    @Override
    public boolean isConstantInit() {
        return init.isConstant();
    }

    @Override
    public boolean isConstantStride() {
        return rawStride.isConstant();
    }

    @Override
    public long constantInit() {
        return init.asJavaConstant().asLong();
    }

    @Override
    public long constantStride() {
        if (op instanceof AddNode) {
            return rawStride.asJavaConstant().asLong();
        }
        if (op instanceof SubNode) {
            return -rawStride.asJavaConstant().asLong();
        }
        throw GraalError.shouldNotReachHere();
    }

    @Override
    public ValueNode extremumNode(boolean assumeLoopEntered, Stamp stamp) {
        Stamp fromStamp = phi.stamp(NodeView.DEFAULT);
        StructuredGraph graph = graph();
        ValueNode stride = strideNode();
        ValueNode initNode = this.initNode();
        if (!fromStamp.isCompatible(stamp)) {
            stride = IntegerConvertNode.convert(stride, stamp, graph(), NodeView.DEFAULT);
            initNode = IntegerConvertNode.convert(initNode, stamp, graph(), NodeView.DEFAULT);
        }
        ValueNode maxTripCount = loop.counted().maxTripCountNode(assumeLoopEntered);
        if (!maxTripCount.stamp(NodeView.DEFAULT).isCompatible(stamp)) {
            maxTripCount = IntegerConvertNode.convert(maxTripCount, stamp, graph(), NodeView.DEFAULT);
        }
        return add(graph, mul(graph, stride, sub(graph, maxTripCount, ConstantNode.forIntegerStamp(stamp, 1, graph))), initNode);
    }

    @Override
    public ValueNode exitValueNode() {
        Stamp stamp = phi.stamp(NodeView.DEFAULT);
        ValueNode maxTripCount = loop.counted().maxTripCountNode();
        if (!maxTripCount.stamp(NodeView.DEFAULT).isCompatible(stamp)) {
            maxTripCount = IntegerConvertNode.convert(maxTripCount, stamp, graph(), NodeView.DEFAULT);
        }
        return add(graph(), mul(graph(), strideNode(), maxTripCount), initNode());
    }

    @Override
    public boolean isConstantExtremum() {
        return isConstantInit() && isConstantStride() && loop.counted().isConstantMaxTripCount();
    }

    @Override
    public long constantExtremum() {
        UnsignedLong tripCount = loop.counted().constantMaxTripCount();
        if (tripCount.isLessThan(1)) {
            return constantInit();
        }
        return tripCount.minus(1).wrappingTimes(constantStride()).wrappingPlus(constantInit()).asLong();
    }

    @Override
    public void deleteUnusedNodes() {
    }

    @Override
    public String toString() {
        return String.format("BasicInductionVariable %s %s %s %s", initNode(), phi, op.getNodeClass().shortName(), strideNode());
    }
}
