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

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_8;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_4;

import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.memory.address.AddressNode;

@NodeInfo(cycles = CYCLES_8, size = SIZE_4)
public class SerialWriteBarrier extends ObjectWriteBarrier {
    public static final NodeClass<SerialWriteBarrier> TYPE = NodeClass.create(SerialWriteBarrier.class);

    protected boolean verifyOnly;

    public SerialWriteBarrier(AddressNode address, boolean precise) {
        this(TYPE, address, precise);
    }

    protected SerialWriteBarrier(NodeClass<? extends SerialWriteBarrier> c, AddressNode address, boolean precise) {
        super(c, address, null, precise);
    }

    public void setVerifyOnly(boolean value) {
        this.verifyOnly = value;
    }

    public boolean getVerifyOnly() {
        return verifyOnly;
    }
}
