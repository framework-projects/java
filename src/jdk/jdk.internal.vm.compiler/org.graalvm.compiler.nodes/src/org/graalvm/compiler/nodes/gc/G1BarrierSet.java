/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, Red Hat Inc. All rights reserved.
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


package org.graalvm.compiler.nodes.gc;

import org.graalvm.compiler.core.common.type.AbstractObjectStamp;
import org.graalvm.compiler.debug.GraalError;
import org.graalvm.compiler.nodes.NodeView;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.extended.ArrayRangeWrite;
import org.graalvm.compiler.nodes.java.AbstractCompareAndSwapNode;
import org.graalvm.compiler.nodes.java.LoweredAtomicReadAndWriteNode;
import org.graalvm.compiler.nodes.memory.FixedAccessNode;
import org.graalvm.compiler.nodes.memory.HeapAccess;
import org.graalvm.compiler.nodes.memory.HeapAccess.BarrierType;
import org.graalvm.compiler.nodes.memory.ReadNode;
import org.graalvm.compiler.nodes.memory.WriteNode;
import org.graalvm.compiler.nodes.memory.address.AddressNode;
import org.graalvm.compiler.nodes.type.StampTool;

public class G1BarrierSet implements BarrierSet {
    public G1BarrierSet() {
    }

    @Override
    public void addBarriers(FixedAccessNode n) {
        if (n instanceof ReadNode) {
            addReadNodeBarriers((ReadNode) n);
        } else if (n instanceof WriteNode) {
            WriteNode write = (WriteNode) n;
            addWriteBarriers(write, write.value(), null, true, write.getNullCheck());
        } else if (n instanceof LoweredAtomicReadAndWriteNode) {
            LoweredAtomicReadAndWriteNode atomic = (LoweredAtomicReadAndWriteNode) n;
            addWriteBarriers(atomic, atomic.getNewValue(), null, true, atomic.getNullCheck());
        } else if (n instanceof AbstractCompareAndSwapNode) {
            AbstractCompareAndSwapNode cmpSwap = (AbstractCompareAndSwapNode) n;
            addWriteBarriers(cmpSwap, cmpSwap.getNewValue(), cmpSwap.getExpectedValue(), false, false);
        } else if (n instanceof ArrayRangeWrite) {
            addArrayRangeBarriers((ArrayRangeWrite) n);
        } else {
            GraalError.guarantee(n.getBarrierType() == BarrierType.NONE, "missed a node that requires a GC barrier: %s", n.getClass());
        }
    }

    private static void addReadNodeBarriers(ReadNode node) {
        if (node.getBarrierType() == HeapAccess.BarrierType.WEAK_FIELD) {
            StructuredGraph graph = node.graph();
            G1ReferentFieldReadBarrier barrier = graph.add(new G1ReferentFieldReadBarrier(node.getAddress(), node, false));
            graph.addAfterFixed(node, barrier);
        }
    }

    private void addWriteBarriers(FixedAccessNode node, ValueNode writtenValue, ValueNode expectedValue, boolean doLoad, boolean nullCheck) {
        HeapAccess.BarrierType barrierType = node.getBarrierType();
        switch (barrierType) {
            case NONE:
                // nothing to do
                break;
            case FIELD:
            case ARRAY:
            case UNKNOWN:
                if (isObjectValue(writtenValue)) {
                    StructuredGraph graph = node.graph();
                    boolean init = node.getLocationIdentity().isInit();
                    if (!init) {
                        // The pre barrier does nothing if the value being read is null, so it can
                        // be explicitly skipped when this is an initializing store.
                        addG1PreWriteBarrier(node, node.getAddress(), expectedValue, doLoad, nullCheck, graph);
                    }
                    if (writeRequiresPostBarrier(node, writtenValue)) {
                        boolean precise = barrierType != HeapAccess.BarrierType.FIELD;
                        addG1PostWriteBarrier(node, node.getAddress(), writtenValue, precise, graph);
                    }
                }
                break;
            default:
                throw new GraalError("unexpected barrier type: " + barrierType);
        }
    }

    @SuppressWarnings("unused")
    protected boolean writeRequiresPostBarrier(FixedAccessNode initializingWrite, ValueNode writtenValue) {
        // Without help from the runtime all writes require an explicit post barrier.
        return true;
    }

    private static void addArrayRangeBarriers(ArrayRangeWrite write) {
        if (write.writesObjectArray()) {
            StructuredGraph graph = write.asNode().graph();
            if (!write.isInitialization()) {
                // The pre barrier does nothing if the value being read is null, so it can
                // be explicitly skipped when this is an initializing store.
                G1ArrayRangePreWriteBarrier g1ArrayRangePreWriteBarrier = graph.add(new G1ArrayRangePreWriteBarrier(write.getAddress(), write.getLength(), write.getElementStride()));
                graph.addBeforeFixed(write.asNode(), g1ArrayRangePreWriteBarrier);
            }
            G1ArrayRangePostWriteBarrier g1ArrayRangePostWriteBarrier = graph.add(new G1ArrayRangePostWriteBarrier(write.getAddress(), write.getLength(), write.getElementStride()));
            graph.addAfterFixed(write.asNode(), g1ArrayRangePostWriteBarrier);
        }
    }

    private static void addG1PreWriteBarrier(FixedAccessNode node, AddressNode address, ValueNode value, boolean doLoad, boolean nullCheck, StructuredGraph graph) {
        G1PreWriteBarrier preBarrier = graph.add(new G1PreWriteBarrier(address, value, doLoad, nullCheck));
        preBarrier.setStateBefore(node.stateBefore());
        node.setNullCheck(false);
        node.setStateBefore(null);
        graph.addBeforeFixed(node, preBarrier);
    }

    private static void addG1PostWriteBarrier(FixedAccessNode node, AddressNode address, ValueNode value, boolean precise, StructuredGraph graph) {
        final boolean alwaysNull = StampTool.isPointerAlwaysNull(value);
        graph.addAfterFixed(node, graph.add(new G1PostWriteBarrier(address, value, precise, alwaysNull)));
    }

    private static boolean isObjectValue(ValueNode value) {
        return value.stamp(NodeView.DEFAULT) instanceof AbstractObjectStamp;
    }
}
