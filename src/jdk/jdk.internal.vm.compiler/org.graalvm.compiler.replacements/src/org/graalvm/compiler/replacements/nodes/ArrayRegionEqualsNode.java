/*
 * Copyright (c) 2013, 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.replacements.nodes;

import jdk.internal.vm.compiler.word.LocationIdentity;
import jdk.internal.vm.compiler.word.Pointer;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.Value;
import org.graalvm.compiler.core.common.spi.ForeignCallLinkage;
import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeCycles;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodeinfo.NodeSize;
import org.graalvm.compiler.nodes.FixedWithNextNode;
import org.graalvm.compiler.nodes.NamedLocationIdentity;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.ValueNodeUtil;
import org.graalvm.compiler.nodes.memory.MemoryAccess;
import org.graalvm.compiler.nodes.memory.MemoryNode;
import org.graalvm.compiler.nodes.spi.LIRLowerable;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;

import static org.graalvm.compiler.core.common.GraalOptions.UseGraalStubs;
import static org.graalvm.compiler.nodeinfo.InputType.Memory;

// JaCoCo Exclude

/**
 * Compares two array regions with a given length.
 */
@NodeInfo(cycles = NodeCycles.CYCLES_UNKNOWN, size = NodeSize.SIZE_128)
public final class ArrayRegionEqualsNode extends FixedWithNextNode implements LIRLowerable, MemoryAccess {

    public static final NodeClass<ArrayRegionEqualsNode> TYPE = NodeClass.create(ArrayRegionEqualsNode.class);

    /** {@link JavaKind} of the arrays to compare. */
    private final JavaKind kind1;
    private final JavaKind kind2;

    /** Pointer to first array region to be tested for equality. */
    @Input private ValueNode array1;

    /** Pointer to second array region to be tested for equality. */
    @Input private ValueNode array2;

    /** Length of the array region. */
    @Input private ValueNode length;

    @OptionalInput(Memory) private MemoryNode lastLocationAccess;

    public ArrayRegionEqualsNode(ValueNode array1, ValueNode array2, ValueNode length, @ConstantNodeParameter JavaKind kind1, @ConstantNodeParameter JavaKind kind2) {
        super(TYPE, StampFactory.forKind(JavaKind.Boolean));
        this.kind1 = kind1;
        this.kind2 = kind2;
        this.array1 = array1;
        this.array2 = array2;
        this.length = length;
    }

    public static boolean regionEquals(Pointer array1, Pointer array2, int length, @ConstantNodeParameter JavaKind kind) {
        return regionEquals(array1, array2, length, kind, kind);
    }

    @NodeIntrinsic
    public static native boolean regionEquals(Pointer array1, Pointer array2, int length, @ConstantNodeParameter JavaKind kind1, @ConstantNodeParameter JavaKind kind2);

    public JavaKind getKind1() {
        return kind1;
    }

    public JavaKind getKind2() {
        return kind2;
    }

    public ValueNode getLength() {
        return length;
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        if (UseGraalStubs.getValue(graph().getOptions())) {
            ForeignCallLinkage linkage = gen.lookupGraalStub(this);
            if (linkage != null) {
                Value result = gen.getLIRGeneratorTool().emitForeignCall(linkage, null, gen.operand(array1), gen.operand(array2), gen.operand(length));
                gen.setResult(this, result);
                return;
            }
        }

        int constantLength = -1;
        if (length.isConstant()) {
            constantLength = length.asJavaConstant().asInt();
        }
        Value result;
        if (kind1 == kind2) {
            result = gen.getLIRGeneratorTool().emitArrayEquals(kind1, gen.operand(array1), gen.operand(array2), gen.operand(length), constantLength, true);
        } else {
            result = gen.getLIRGeneratorTool().emitArrayEquals(kind1, kind2, gen.operand(array1), gen.operand(array2), gen.operand(length), constantLength, true);
        }
        gen.setResult(this, result);
    }

    @Override
    public LocationIdentity getLocationIdentity() {
        return kind1 != kind2 ? LocationIdentity.ANY_LOCATION : NamedLocationIdentity.getArrayLocation(kind1);
    }

    @Override
    public MemoryNode getLastLocationAccess() {
        return lastLocationAccess;
    }

    @Override
    public void setLastLocationAccess(MemoryNode lla) {
        updateUsages(ValueNodeUtil.asNode(lastLocationAccess), ValueNodeUtil.asNode(lla));
        lastLocationAccess = lla;
    }
}
