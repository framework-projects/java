/*
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.nodes.extended;

import static org.graalvm.compiler.nodeinfo.InputType.Guard;

import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.ValueNode;
import jdk.internal.vm.compiler.word.LocationIdentity;

import jdk.vm.ci.meta.JavaKind;

@NodeInfo
public class GuardedUnsafeLoadNode extends RawLoadNode implements GuardedNode {
    public static final NodeClass<GuardedUnsafeLoadNode> TYPE = NodeClass.create(GuardedUnsafeLoadNode.class);

    @OptionalInput(Guard) protected GuardingNode guard;

    public GuardedUnsafeLoadNode(ValueNode object, ValueNode offset, JavaKind accessKind, LocationIdentity locationIdentity, ValueNode guard) {
        super(TYPE, object, offset, accessKind, locationIdentity);
        this.guard = (GuardingNode) guard;
    }

    @Override
    public GuardingNode getGuard() {
        return guard;
    }

    @Override
    public void setGuard(GuardingNode guard) {
        updateUsagesInterface(this.guard, guard);
        this.guard = guard;
    }

    @NodeIntrinsic
    public static native Object guardedLoad(Object object, long offset, @ConstantNodeParameter JavaKind kind, @ConstantNodeParameter LocationIdentity locationIdentity, GuardingNode guard);
}
