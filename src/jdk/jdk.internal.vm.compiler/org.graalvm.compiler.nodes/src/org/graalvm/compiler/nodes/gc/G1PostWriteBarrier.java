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


package org.graalvm.compiler.nodes.gc;

import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.memory.address.AddressNode;

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_64;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_64;

@NodeInfo(cycles = CYCLES_64, size = SIZE_64)
public class G1PostWriteBarrier extends ObjectWriteBarrier {

    public static final NodeClass<G1PostWriteBarrier> TYPE = NodeClass.create(G1PostWriteBarrier.class);
    protected final boolean alwaysNull;

    public G1PostWriteBarrier(AddressNode address, ValueNode value, boolean precise, boolean alwaysNull) {
        this(TYPE, address, value, precise, alwaysNull);
    }

    private G1PostWriteBarrier(NodeClass<? extends G1PostWriteBarrier> c, AddressNode address, ValueNode value, boolean precise, boolean alwaysNull) {
        super(c, address, value, precise);
        this.alwaysNull = alwaysNull;
    }

    public boolean alwaysNull() {
        return alwaysNull;
    }
}
