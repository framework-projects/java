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


package org.graalvm.compiler.hotspot.sparc;

import jdk.vm.ci.code.BytecodeFrame;
import jdk.vm.ci.code.CallingConvention;
import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.RegisterValue;
import jdk.vm.ci.hotspot.HotSpotCallingConventionType;
import jdk.vm.ci.hotspot.HotSpotResolvedJavaMethod;
import jdk.vm.ci.meta.AllocatableValue;
import jdk.vm.ci.meta.JavaType;
import jdk.vm.ci.meta.Value;
import jdk.vm.ci.sparc.SPARCKind;
import org.graalvm.compiler.core.common.LIRKind;
import org.graalvm.compiler.core.common.spi.ForeignCallLinkage;
import org.graalvm.compiler.core.gen.DebugInfoBuilder;
import org.graalvm.compiler.core.sparc.SPARCNodeLIRBuilder;
import org.graalvm.compiler.core.sparc.SPARCNodeMatchRules;
import org.graalvm.compiler.hotspot.HotSpotDebugInfoBuilder;
import org.graalvm.compiler.hotspot.HotSpotLIRGenerator;
import org.graalvm.compiler.hotspot.HotSpotLockStack;
import org.graalvm.compiler.hotspot.HotSpotNodeLIRBuilder;
import org.graalvm.compiler.hotspot.nodes.HotSpotDirectCallTargetNode;
import org.graalvm.compiler.hotspot.nodes.HotSpotIndirectCallTargetNode;
import org.graalvm.compiler.lir.LIRFrameState;
import org.graalvm.compiler.lir.Variable;
import org.graalvm.compiler.lir.gen.LIRGeneratorTool;
import org.graalvm.compiler.lir.sparc.SPARCBreakpointOp;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.CallTargetNode.InvokeKind;
import org.graalvm.compiler.nodes.spi.NodeValueMap;

import static jdk.vm.ci.sparc.SPARC.g5;
import static jdk.vm.ci.sparc.SPARC.o7;
import static org.graalvm.compiler.hotspot.HotSpotBackend.EXCEPTION_HANDLER_IN_CALLER;

public class SPARCHotSpotNodeLIRBuilder extends SPARCNodeLIRBuilder implements HotSpotNodeLIRBuilder {

    public SPARCHotSpotNodeLIRBuilder(StructuredGraph graph, LIRGeneratorTool lirGen, SPARCNodeMatchRules nodeMatchRules) {
        super(graph, lirGen, nodeMatchRules);
        assert gen instanceof SPARCHotSpotLIRGenerator;
        assert getDebugInfoBuilder() instanceof HotSpotDebugInfoBuilder;
        ((SPARCHotSpotLIRGenerator) gen).setDebugInfoBuilder(((HotSpotDebugInfoBuilder) getDebugInfoBuilder()));
    }

    @Override
    protected DebugInfoBuilder createDebugInfoBuilder(StructuredGraph graph, NodeValueMap nodeValueMap) {
        HotSpotLockStack lockStack = new HotSpotLockStack(gen.getResult().getFrameMapBuilder(), LIRKind.value(SPARCKind.XWORD));
        return new HotSpotDebugInfoBuilder(nodeValueMap, lockStack, (HotSpotLIRGenerator) gen);
    }

    private SPARCHotSpotLIRGenerator getGen() {
        return (SPARCHotSpotLIRGenerator) gen;
    }

    @Override
    public void visitSafepointNode(SafepointNode i) {
        LIRFrameState info = state(i);
        Register thread = getGen().getProviders().getRegisters().getThreadRegister();
        append(new SPARCHotSpotSafepointOp(info, getGen().config, thread, gen));
    }

    @Override
    protected void emitDirectCall(DirectCallTargetNode callTarget, Value result, Value[] parameters, Value[] temps, LIRFrameState callState) {
        InvokeKind invokeKind = ((HotSpotDirectCallTargetNode) callTarget).invokeKind();
        if (invokeKind.isIndirect()) {
            append(new SPARCHotspotDirectVirtualCallOp(callTarget.targetMethod(), result, parameters, temps, callState, invokeKind, getGen().config));
        } else {
            assert invokeKind.isDirect();
            HotSpotResolvedJavaMethod resolvedMethod = (HotSpotResolvedJavaMethod) callTarget.targetMethod();
            assert resolvedMethod.isConcrete() : "Cannot make direct call to abstract method.";
            append(new SPARCHotspotDirectStaticCallOp(callTarget.targetMethod(), result, parameters, temps, callState, invokeKind, getGen().config));
        }
    }

    @Override
    protected void emitIndirectCall(IndirectCallTargetNode callTarget, Value result, Value[] parameters, Value[] temps, LIRFrameState callState) {
        Value metaspaceMethodSrc = operand(((HotSpotIndirectCallTargetNode) callTarget).metaspaceMethod());
        AllocatableValue metaspaceMethod = g5.asValue(metaspaceMethodSrc.getValueKind());
        gen.emitMove(metaspaceMethod, metaspaceMethodSrc);

        Value targetAddressSrc = operand(callTarget.computedAddress());
        AllocatableValue targetAddress = o7.asValue(targetAddressSrc.getValueKind());
        gen.emitMove(targetAddress, targetAddressSrc);
        append(new SPARCIndirectCallOp(callTarget.targetMethod(), result, parameters, temps, metaspaceMethod, targetAddress, callState, getGen().config));
    }

    @Override
    public void emitPatchReturnAddress(ValueNode address) {
        append(new SPARCHotSpotPatchReturnAddressOp(gen.load(operand(address))));
    }

    @Override
    public void emitJumpToExceptionHandler(ValueNode address) {
        append(new SPARCHotSpotJumpToExceptionHandlerOp(gen.load(operand(address))));
    }

    @Override
    public void emitJumpToExceptionHandlerInCaller(ValueNode handlerInCallerPc, ValueNode exception, ValueNode exceptionPc) {
        Variable handler = gen.load(operand(handlerInCallerPc));
        ForeignCallLinkage linkage = gen.getForeignCalls().lookupForeignCall(EXCEPTION_HANDLER_IN_CALLER);
        CallingConvention linkageCc = linkage.getOutgoingCallingConvention();
        assert linkageCc.getArgumentCount() == 2;
        RegisterValue exceptionFixed = (RegisterValue) linkageCc.getArgument(0);
        RegisterValue exceptionPcFixed = (RegisterValue) linkageCc.getArgument(1);
        gen.emitMove(exceptionFixed, operand(exception));
        gen.emitMove(exceptionPcFixed, operand(exceptionPc));
        Register thread = getGen().getProviders().getRegisters().getThreadRegister();
        SPARCHotSpotJumpToExceptionHandlerInCallerOp op = new SPARCHotSpotJumpToExceptionHandlerInCallerOp(handler, exceptionFixed, exceptionPcFixed, getGen().config.threadIsMethodHandleReturnOffset,
                        thread);
        append(op);
    }

    @Override
    protected void emitPrologue(StructuredGraph graph) {
        super.emitPrologue(graph);
        SPARCHotSpotSafepointOp.emitPrologue(this, getGen());
    }

    @Override
    public void visitFullInfopointNode(FullInfopointNode i) {
        if (i.getState() != null && i.getState().bci == BytecodeFrame.AFTER_BCI) {
            i.getDebug().log("Ignoring InfopointNode for AFTER_BCI");
        } else {
            super.visitFullInfopointNode(i);
        }
    }

    @Override
    public void visitBreakpointNode(BreakpointNode node) {
        JavaType[] sig = new JavaType[node.arguments().size()];
        for (int i = 0; i < sig.length; i++) {
            sig[i] = node.arguments().get(i).stamp(NodeView.DEFAULT).javaType(gen.getMetaAccess());
        }

        Value[] parameters = visitInvokeArguments(gen.getRegisterConfig().getCallingConvention(HotSpotCallingConventionType.JavaCall, null, sig, gen), node.arguments());
        append(new SPARCBreakpointOp(parameters));
    }
}
