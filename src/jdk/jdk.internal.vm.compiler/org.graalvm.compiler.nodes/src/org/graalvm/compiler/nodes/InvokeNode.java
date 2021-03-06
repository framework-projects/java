/*
 * Copyright (c) 2009, 2018, Oracle and/or its affiliates. All rights reserved.
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

import jdk.internal.vm.compiler.word.LocationIdentity;
import jdk.vm.ci.code.BytecodeFrame;
import jdk.vm.ci.meta.JavaKind;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.*;
import org.graalvm.compiler.nodes.java.MethodCallTargetNode;
import org.graalvm.compiler.nodes.memory.AbstractMemoryCheckpoint;
import org.graalvm.compiler.nodes.memory.MemoryCheckpoint;
import org.graalvm.compiler.nodes.spi.LIRLowerable;
import org.graalvm.compiler.nodes.spi.LoweringTool;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;
import org.graalvm.compiler.nodes.spi.UncheckedInterfaceProvider;

import java.util.Map;

import static org.graalvm.compiler.nodeinfo.InputType.*;
import static org.graalvm.compiler.nodeinfo.NodeCycles.*;
import static org.graalvm.compiler.nodeinfo.NodeSize.*;

/**
 * The {@code InvokeNode} represents all kinds of method calls.
 */
// @formatter:off
@NodeInfo(nameTemplate = "Invoke#{p#targetMethod/s}",
          allowedUsageTypes = {Memory},
          cycles = CYCLES_UNKNOWN,
          cyclesRationale = "We cannot estimate the runtime cost of a call, it is a blackhole." +
                            "However, we can estimate, dynamically, the cost of the call operation itself based on the type of the call.",
          size = SIZE_UNKNOWN,
          sizeRationale = "We can only dynamically, based on the type of the call (special, static, virtual, interface) decide" +
                          "how much code is generated for the call.")
// @formatter:on
public final class InvokeNode extends AbstractMemoryCheckpoint implements Invoke, LIRLowerable, MemoryCheckpoint.Single, UncheckedInterfaceProvider {
    public static final NodeClass<InvokeNode> TYPE = NodeClass.create(InvokeNode.class);

    @OptionalInput ValueNode classInit;
    @Input(Extension) CallTargetNode callTarget;
    @OptionalInput(State) FrameState stateDuring;
    protected int bci;
    protected boolean polymorphic;
    protected boolean useForInlining;
    protected final LocationIdentity identity;

    public InvokeNode(CallTargetNode callTarget, int bci) {
        this(callTarget, bci, callTarget.returnStamp().getTrustedStamp());
    }

    public InvokeNode(CallTargetNode callTarget, int bci, LocationIdentity identity) {
        this(callTarget, bci, callTarget.returnStamp().getTrustedStamp(), identity);
    }

    public InvokeNode(CallTargetNode callTarget, int bci, Stamp stamp) {
        this(callTarget, bci, stamp, LocationIdentity.any());
    }

    public InvokeNode(CallTargetNode callTarget, int bci, Stamp stamp, LocationIdentity identity) {
        super(TYPE, stamp);
        this.callTarget = callTarget;
        this.bci = bci;
        this.polymorphic = false;
        this.useForInlining = true;
        this.identity = identity;
    }

    public InvokeNode(InvokeWithExceptionNode invoke) {
        super(TYPE, invoke.stamp);
        this.callTarget = invoke.callTarget;
        this.bci = invoke.bci;
        this.polymorphic = invoke.polymorphic;
        this.useForInlining = invoke.useForInlining;
        this.identity = invoke.getLocationIdentity();
    }

    @Override
    public void replaceBci(int newBci) {
        assert BytecodeFrame.isPlaceholderBci(bci) && !BytecodeFrame.isPlaceholderBci(newBci) : "can only replace placeholder with better bci";
        bci = newBci;
    }

    @Override
    protected void afterClone(Node other) {
        updateInliningLogAfterClone(other);
    }

    @Override
    public FixedNode asFixedNode() {
        return this;
    }

    @Override
    public CallTargetNode callTarget() {
        return callTarget;
    }

    void setCallTarget(CallTargetNode callTarget) {
        updateUsages(this.callTarget, callTarget);
        this.callTarget = callTarget;
    }

    @Override
    public boolean isPolymorphic() {
        return polymorphic;
    }

    @Override
    public void setPolymorphic(boolean value) {
        this.polymorphic = value;
    }

    @Override
    public boolean useForInlining() {
        return useForInlining;
    }

    @Override
    public void setUseForInlining(boolean value) {
        this.useForInlining = value;
    }

    @Override
    public boolean isAllowedUsageType(InputType type) {
        if (!super.isAllowedUsageType(type)) {
            if (getStackKind() != JavaKind.Void) {
                if (callTarget instanceof MethodCallTargetNode && ((MethodCallTargetNode) callTarget).targetMethod().getAnnotation(NodeIntrinsic.class) != null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public Map<Object, Object> getDebugProperties(Map<Object, Object> map) {
        Map<Object, Object> debugProperties = super.getDebugProperties(map);
        if (callTarget != null) {
            debugProperties.put("targetMethod", callTarget.targetName());
        }
        return debugProperties;
    }

    @Override
    public LocationIdentity getLocationIdentity() {
        return identity;
    }

    @Override
    public void lower(LoweringTool tool) {
        tool.getLowerer().lower(this, tool);
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        gen.emitInvoke(this);
    }

    @Override
    public String toString(Verbosity verbosity) {
        if (verbosity == Verbosity.Long) {
            return super.toString(Verbosity.Short) + "(bci=" + bci() + ")";
        } else if (verbosity == Verbosity.Name) {
            return "Invoke#" + (callTarget == null ? "null" : callTarget().targetName());
        } else {
            return super.toString(verbosity);
        }
    }

    @Override
    public int bci() {
        return bci;
    }

    @Override
    public boolean canDeoptimize() {
        return true;
    }

    @Override
    public FrameState stateDuring() {
        return stateDuring;
    }

    @Override
    public void setStateDuring(FrameState stateDuring) {
        updateUsages(this.stateDuring, stateDuring);
        this.stateDuring = stateDuring;
    }

    @Override
    public Stamp uncheckedStamp() {
        return this.callTarget.returnStamp().getUncheckedStamp();
    }

    @Override
    public void setClassInit(ValueNode classInit) {
        this.classInit = classInit;
        updateUsages(null, classInit);
    }

    @Override
    public ValueNode classInit() {
        return classInit;
    }

    @Override
    public NodeCycles estimatedNodeCycles() {
        if (callTarget == null) {
            return CYCLES_UNKNOWN;
        }
        switch (callTarget.invokeKind()) {
            case Interface:
                return CYCLES_64;
            case Special:
            case Static:
                return CYCLES_2;
            case Virtual:
                return CYCLES_8;
            default:
                return CYCLES_UNKNOWN;
        }
    }

    @Override
    public NodeSize estimatedNodeSize() {
        if (callTarget == null) {
            return SIZE_UNKNOWN;
        }
        switch (callTarget.invokeKind()) {
            case Interface:
                return SIZE_64;
            case Special:
            case Static:
                return SIZE_2;
            case Virtual:
                return SIZE_8;
            default:
                return SIZE_UNKNOWN;
        }
    }
}
