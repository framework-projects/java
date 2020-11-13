/*
 * Copyright (c) 2011, 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.nodes.spi;

import jdk.vm.ci.code.CallingConvention;
import jdk.vm.ci.meta.Value;
import org.graalvm.compiler.core.common.cfg.BlockMap;
import org.graalvm.compiler.core.common.spi.ForeignCallLinkage;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeSourcePosition;
import org.graalvm.compiler.lir.LIRFrameState;
import org.graalvm.compiler.lir.gen.LIRGeneratorTool;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.calc.ConditionalNode;
import org.graalvm.compiler.nodes.cfg.Block;
import org.graalvm.compiler.nodes.extended.SwitchNode;
import org.graalvm.compiler.options.OptionValues;

import java.util.Collection;
import java.util.List;

public interface NodeLIRBuilderTool extends NodeValueMap {

    // TODO (je) remove and move into the Node
    LIRFrameState state(DeoptimizingNode deopt);

    void emitIf(IfNode i);

    void emitConditional(ConditionalNode i);

    void emitSwitch(SwitchNode i);

    void emitInvoke(Invoke i);

    // Handling of block-end nodes still needs to be unified in the LIRGenerator.
    void visitMerge(AbstractMergeNode i);

    void visitEndNode(AbstractEndNode i);

    void visitLoopEnd(LoopEndNode i);

    // These methods define the contract a runtime specific backend must provide.

    void visitSafepointNode(SafepointNode i);

    void visitBreakpointNode(BreakpointNode i);

    void visitFullInfopointNode(FullInfopointNode i);

    void setSourcePosition(NodeSourcePosition position);

    LIRGeneratorTool getLIRGeneratorTool();

    void emitOverflowCheckBranch(AbstractBeginNode overflowSuccessor, AbstractBeginNode next, Stamp compareStamp, double probability);

    Value[] visitInvokeArguments(CallingConvention cc, Collection<ValueNode> arguments);

    void doBlock(Block block, StructuredGraph graph, BlockMap<List<Node>> blockMap);

    default OptionValues getOptions() {
        return getLIRGeneratorTool().getResult().getLIR().getOptions();
    }

    void emitReadExceptionObject(ValueNode node);

    @SuppressWarnings("unused")
    default ForeignCallLinkage lookupGraalStub(ValueNode valueNode) {
        return null;
    }

    void matchBlock(Block b, StructuredGraph graph, StructuredGraph.ScheduleResult blockMap);
}
