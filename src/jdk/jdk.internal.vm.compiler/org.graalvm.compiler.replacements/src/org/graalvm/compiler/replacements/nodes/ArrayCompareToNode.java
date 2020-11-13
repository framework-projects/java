/*
 * Copyright (c) 2017, 2019, Oracle and/or its affiliates. All rights reserved.
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
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.Value;
import org.graalvm.compiler.core.common.spi.ForeignCallLinkage;
import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.Canonicalizable;
import org.graalvm.compiler.graph.spi.CanonicalizerTool;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.memory.MemoryAccess;
import org.graalvm.compiler.nodes.memory.MemoryNode;
import org.graalvm.compiler.nodes.spi.LIRLowerable;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;
import org.graalvm.compiler.nodes.spi.Virtualizable;
import org.graalvm.compiler.nodes.spi.VirtualizerTool;
import org.graalvm.compiler.nodes.util.GraphUtil;

import static org.graalvm.compiler.core.common.GraalOptions.UseGraalStubs;
import static org.graalvm.compiler.nodeinfo.InputType.Memory;
import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_1024;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_1024;

// JaCoCo Exclude

/**
 * Compares two arrays lexicographically.
 */
@NodeInfo(cycles = CYCLES_1024, size = SIZE_1024)
public final class ArrayCompareToNode extends FixedWithNextNode implements LIRLowerable, Canonicalizable, Virtualizable, MemoryAccess {

    public static final NodeClass<ArrayCompareToNode> TYPE = NodeClass.create(ArrayCompareToNode.class);

    /** {@link JavaKind} of one array to compare. */
    protected final JavaKind kind1;

    /** {@link JavaKind} of the other array to compare. */
    protected final JavaKind kind2;

    /** One array to be tested for equality. */
    @Input ValueNode array1;

    /** The other array to be tested for equality. */
    @Input ValueNode array2;

    /** Length of one array. */
    @Input ValueNode length1;

    /** Length of the other array. */
    @Input ValueNode length2;

    @OptionalInput(Memory) MemoryNode lastLocationAccess;

    public ArrayCompareToNode(ValueNode array1, ValueNode array2, ValueNode length1, ValueNode length2, @ConstantNodeParameter JavaKind kind1, @ConstantNodeParameter JavaKind kind2) {
        super(TYPE, StampFactory.forKind(JavaKind.Int));
        this.kind1 = kind1;
        this.kind2 = kind2;
        this.array1 = array1;
        this.array2 = array2;
        this.length1 = length1;
        this.length2 = length2;
    }

    @Override
    public Node canonical(CanonicalizerTool tool) {
        if (tool.allUsagesAvailable() && hasNoUsages()) {
            return null;
        }
        ValueNode a1 = GraphUtil.unproxify(array1);
        ValueNode a2 = GraphUtil.unproxify(array2);
        if (a1 == a2) {
            return ConstantNode.forInt(0);
        }
        return this;
    }

    @Override
    public void virtualize(VirtualizerTool tool) {
        ValueNode alias1 = tool.getAlias(array1);
        ValueNode alias2 = tool.getAlias(array2);
        if (alias1 == alias2) {
            // the same virtual objects will always have the same contents
            tool.replaceWithValue(ConstantNode.forInt(0, graph()));
        }
    }

    @NodeIntrinsic
    public static native int compareTo(Object array1, Object array2, int length1, int length2, @ConstantNodeParameter JavaKind kind1, @ConstantNodeParameter JavaKind kind2);

    public JavaKind getKind1() {
        return kind1;
    }

    public JavaKind getKind2() {
        return kind2;
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        if (UseGraalStubs.getValue(graph().getOptions())) {
            ForeignCallLinkage linkage = gen.lookupGraalStub(this);
            if (linkage != null) {
                Value result = gen.getLIRGeneratorTool().emitForeignCall(linkage, null, gen.operand(array1), gen.operand(array2), gen.operand(length1), gen.operand(length2));
                gen.setResult(this, result);
                return;
            }
        }

        Value result = gen.getLIRGeneratorTool().emitArrayCompareTo(kind1, kind2, gen.operand(array1), gen.operand(array2), gen.operand(length1), gen.operand(length2));
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