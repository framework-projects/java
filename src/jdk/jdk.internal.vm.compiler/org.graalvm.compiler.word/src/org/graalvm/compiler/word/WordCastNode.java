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


package org.graalvm.compiler.word;

import jdk.vm.ci.meta.*;
import org.graalvm.compiler.core.common.LIRKind;
import org.graalvm.compiler.core.common.type.*;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.Canonicalizable;
import org.graalvm.compiler.graph.spi.CanonicalizerTool;
import org.graalvm.compiler.lir.ConstantValue;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.FixedWithNextNode;
import org.graalvm.compiler.nodes.NodeView;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.spi.LIRLowerable;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;
import org.graalvm.compiler.nodes.type.NarrowOopStamp;

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_1;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_1;

/**
 * Casts between Word and Object exposed by the {@link Word#fromAddress},
 * {@link Word#objectToTrackedPointer}, {@link Word#objectToUntrackedPointer} and
 * {@link Word#toObject()} operations. It has an impact on the pointer maps for the GC, so it must
 * not be scheduled or optimized away.
 */
@NodeInfo(cycles = CYCLES_1, size = SIZE_1)
public final class WordCastNode extends FixedWithNextNode implements LIRLowerable, Canonicalizable {

    public static final NodeClass<WordCastNode> TYPE = NodeClass.create(WordCastNode.class);

    @Input ValueNode input;
    private final boolean trackedPointer;

    public static WordCastNode wordToObject(ValueNode input, JavaKind wordKind) {
        assert input.getStackKind() == wordKind;
        return new WordCastNode(objectStampFor(input), input);
    }

    public static WordCastNode wordToObjectNonNull(ValueNode input, JavaKind wordKind) {
        assert input.getStackKind() == wordKind;
        return new WordCastNode(StampFactory.objectNonNull(), input);
    }

    public static WordCastNode wordToNarrowObject(ValueNode input, NarrowOopStamp stamp) {
        return new WordCastNode(stamp, input);
    }

    public static WordCastNode addressToWord(ValueNode input, JavaKind wordKind) {
        assert input.stamp(NodeView.DEFAULT) instanceof AbstractPointerStamp;
        return new WordCastNode(StampFactory.forKind(wordKind), input);
    }

    public static WordCastNode objectToTrackedPointer(ValueNode input, JavaKind wordKind) {
        assert input.stamp(NodeView.DEFAULT) instanceof ObjectStamp;
        return new WordCastNode(StampFactory.forKind(wordKind), input);
    }

    public static WordCastNode objectToUntrackedPointer(ValueNode input, JavaKind wordKind) {
        assert input.stamp(NodeView.DEFAULT) instanceof ObjectStamp;
        return new WordCastNode(StampFactory.forKind(wordKind), input, false);
    }

    public static WordCastNode narrowOopToUntrackedWord(ValueNode input, JavaKind wordKind) {
        assert input.stamp(NodeView.DEFAULT) instanceof NarrowOopStamp;
        return new WordCastNode(StampFactory.forKind(wordKind), input, false);
    }

    private WordCastNode(Stamp stamp, ValueNode input) {
        this(stamp, input, true);
    }

    protected WordCastNode(Stamp stamp, ValueNode input, boolean trackedPointer) {
        super(TYPE, stamp);
        this.input = input;
        this.trackedPointer = trackedPointer;
    }

    public ValueNode getInput() {
        return input;
    }

    private static boolean isZeroConstant(ValueNode value) {
        JavaConstant constant = value.asJavaConstant();
        return constant.getJavaKind().isNumericInteger() && constant.asLong() == 0;
    }

    private static Stamp objectStampFor(ValueNode input) {
        Stamp inputStamp = input.stamp(NodeView.DEFAULT);
        if (inputStamp instanceof AbstractPointerStamp) {
            AbstractPointerStamp pointerStamp = (AbstractPointerStamp) inputStamp;
            if (pointerStamp.alwaysNull()) {
                return StampFactory.alwaysNull();
            } else if (pointerStamp.nonNull()) {
                return StampFactory.objectNonNull();
            }
        } else if (inputStamp instanceof IntegerStamp && !((IntegerStamp) inputStamp).contains(0)) {
            return StampFactory.objectNonNull();
        } else if (input.isConstant() && isZeroConstant(input)) {
            return StampFactory.alwaysNull();
        }
        return StampFactory.object();
    }

    @Override
    public boolean inferStamp() {
        if (stamp instanceof AbstractPointerStamp) {
            AbstractPointerStamp objectStamp = (AbstractPointerStamp) stamp;
            if (!objectStamp.alwaysNull() && !objectStamp.nonNull()) {
                Stamp newStamp = stamp;
                Stamp inputStamp = input.stamp(NodeView.DEFAULT);
                if (inputStamp instanceof AbstractPointerStamp) {
                    AbstractPointerStamp pointerStamp = (AbstractPointerStamp) inputStamp;
                    if (pointerStamp.alwaysNull()) {
                        newStamp = objectStamp.asAlwaysNull();
                    } else if (pointerStamp.nonNull()) {
                        newStamp = objectStamp.asNonNull();
                    }
                } else if (inputStamp instanceof IntegerStamp && !((IntegerStamp) inputStamp).contains(0)) {
                    newStamp = objectStamp.asNonNull();
                } else if (input.isConstant() && isZeroConstant(input)) {
                    newStamp = objectStamp.asAlwaysNull();
                }
                return updateStamp(newStamp);
            }
        }
        return false;
    }

    @Override
    public Node canonical(CanonicalizerTool tool) {
        if (tool.allUsagesAvailable() && hasNoUsages()) {
            /* If the cast is unused, it can be eliminated. */
            return input;
        }

        assert !stamp.isCompatible(input.stamp(NodeView.DEFAULT));
        if (input.isConstant()) {
            /* Null pointers are uncritical for GC, so they can be constant folded. */
            if (input.asJavaConstant().isNull()) {
                return ConstantNode.forIntegerStamp(stamp, 0);
            } else if (isZeroConstant(input)) {
                return ConstantNode.forConstant(stamp, ((AbstractPointerStamp) stamp).nullConstant(), tool.getMetaAccess());
            }
        }

        return this;
    }

    @Override
    public void generate(NodeLIRBuilderTool generator) {
        Value value = generator.operand(input);
        ValueKind<?> kind = generator.getLIRGeneratorTool().getLIRKind(stamp(NodeView.DEFAULT));
        assert kind.getPlatformKind().getSizeInBytes() == value.getPlatformKind().getSizeInBytes();

        if (trackedPointer && LIRKind.isValue(kind) && !LIRKind.isValue(value)) {
            // just change the PlatformKind, but don't drop reference information
            kind = value.getValueKind().changeType(kind.getPlatformKind());
        }

        if (kind.equals(value.getValueKind()) && !(value instanceof ConstantValue)) {
            generator.setResult(this, value);
        } else {
            AllocatableValue result = generator.getLIRGeneratorTool().newVariable(kind);
            if (stamp.equals(StampFactory.object())) {
                generator.getLIRGeneratorTool().emitConvertZeroToNull(result, value);
            } else if (!trackedPointer && !((AbstractPointerStamp) input.stamp(NodeView.DEFAULT)).nonNull()) {
                generator.getLIRGeneratorTool().emitConvertNullToZero(result, value);
            } else {
                generator.getLIRGeneratorTool().emitMove(result, value);
            }
            generator.setResult(this, result);
        }
    }
}