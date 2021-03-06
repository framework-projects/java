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


package org.graalvm.compiler.nodes;

import jdk.vm.ci.code.CallingConvention;
import jdk.vm.ci.meta.JavaType;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.core.common.type.StampPair;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;

@NodeInfo
public class DirectCallTargetNode extends LoweredCallTargetNode {

    public static final NodeClass<DirectCallTargetNode> TYPE = NodeClass.create(DirectCallTargetNode.class);

    public DirectCallTargetNode(ValueNode[] arguments, StampPair returnStamp, JavaType[] signature, ResolvedJavaMethod target, CallingConvention.Type callType,
                    InvokeKind invokeKind) {
        this(TYPE, arguments, returnStamp, signature, target, callType, invokeKind);
    }

    protected DirectCallTargetNode(NodeClass<? extends DirectCallTargetNode> c, ValueNode[] arguments, StampPair returnStamp, JavaType[] signature, ResolvedJavaMethod target,
                    CallingConvention.Type callType, InvokeKind invokeKind) {
        super(c, arguments, returnStamp, signature, target, callType, invokeKind);
    }

    @Override
    public String targetName() {
        return targetMethod().format("Direct#%h.%n");
    }
}
