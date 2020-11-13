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


package org.graalvm.compiler.nodes.virtual;

import jdk.vm.ci.meta.ConstantReflectionProvider;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.MetaAccessProvider;
import jdk.vm.ci.meta.ResolvedJavaType;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodeinfo.Verbosity;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.FixedNode;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.spi.ArrayLengthProvider;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;

import java.nio.ByteOrder;

@NodeInfo(nameTemplate = "VirtualArray({p#objectId}) {p#componentType/s}[{p#length}]")
public class VirtualArrayNode extends VirtualObjectNode implements ArrayLengthProvider {

    public static final NodeClass<VirtualArrayNode> TYPE = NodeClass.create(VirtualArrayNode.class);
    protected final ResolvedJavaType componentType;
    protected final int length;

    public VirtualArrayNode(ResolvedJavaType componentType, int length) {
        this(TYPE, componentType, length);
    }

    protected VirtualArrayNode(NodeClass<? extends VirtualObjectNode> c, ResolvedJavaType componentType, int length) {
        super(c, componentType.getArrayClass(), true);
        this.componentType = componentType;
        this.length = length;
    }

    @Override
    public ResolvedJavaType type() {
        return componentType.getArrayClass();
    }

    public ResolvedJavaType componentType() {
        return componentType;
    }

    @Override
    public int entryCount() {
        return length;
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        // nothing to do...
    }

    @Override
    public String toString(Verbosity verbosity) {
        if (verbosity == Verbosity.Name) {
            return super.toString(Verbosity.Name) + "(" + getObjectId() + ") " + componentType.getName() + "[" + length + "]";
        } else {
            return super.toString(verbosity);
        }
    }

    @Override
    public String entryName(int index) {
        return "[" + index + "]";
    }

    @Override
    public int entryIndexForOffset(MetaAccessProvider metaAccess, long constantOffset, JavaKind expectedEntryKind) {
        return entryIndexForOffset(metaAccess, constantOffset, expectedEntryKind, componentType, length);
    }

    public static int entryIndexForOffset(MetaAccessProvider metaAccess, long constantOffset, JavaKind expectedEntryKind, ResolvedJavaType componentType, int length) {
        int baseOffset = metaAccess.getArrayBaseOffset(componentType.getJavaKind());
        int indexScale = metaAccess.getArrayIndexScale(componentType.getJavaKind());

        long offset;
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN && componentType.isPrimitive()) {
            // On big endian, we expect the value to be correctly aligned in memory
            int componentByteCount = componentType.getJavaKind().getByteCount();
            offset = constantOffset - (componentByteCount - Math.min(componentByteCount, 4 + expectedEntryKind.getByteCount()));
        } else {
            offset = constantOffset;
        }
        long index = offset - baseOffset;
        if (index % indexScale != 0) {
            return -1;
        }
        long elementIndex = index / indexScale;
        if (elementIndex < 0 || elementIndex >= length) {
            return -1;
        }
        return (int) elementIndex;
    }

    @Override
    public JavaKind entryKind(int index) {
        assert index >= 0 && index < length;
        return componentType.getJavaKind();
    }

    @Override
    public VirtualArrayNode duplicate() {
        VirtualArrayNode node = new VirtualArrayNode(componentType, length);
        node.setNodeSourcePosition(this.getNodeSourcePosition());
        return node;
    }

    @Override
    public ValueNode getMaterializedRepresentation(FixedNode fixed, ValueNode[] entries, LockState locks) {
        AllocatedObjectNode node = new AllocatedObjectNode(this);
        node.setNodeSourcePosition(this.getNodeSourcePosition());
        return node;
    }

    @Override
    public ValueNode findLength(FindLengthMode mode, ConstantReflectionProvider constantReflection) {
        return ConstantNode.forInt(length);
    }
}