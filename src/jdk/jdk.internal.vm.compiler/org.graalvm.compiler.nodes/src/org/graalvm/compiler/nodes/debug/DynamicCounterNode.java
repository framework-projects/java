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


package org.graalvm.compiler.nodes.debug;

import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.debug.GraalError;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.lir.LIRInstruction;
import org.graalvm.compiler.lir.gen.LIRGeneratorTool;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.spi.LIRLowerable;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_IGNORED;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_IGNORED;

/**
 * This node can be used to add a counter to the code that will estimate the dynamic number of calls
 * by adding an increment to the compiled code. This should of course only be used for
 * debugging/testing purposes.
 *
 * A unique counter will be created for each unique name passed to the constructor. Depending on the
 * value of withContext, the name of the root method is added to the counter's name.
 */
//@formatter:off
@NodeInfo(size = SIZE_IGNORED,
          sizeRationale = "Node is a debugging node that should not be used in production.",
          cycles = CYCLES_IGNORED,
          cyclesRationale = "Node is a debugging node that should not be used in production.")
//@formatter:on
public class DynamicCounterNode extends FixedWithNextNode implements LIRLowerable {

    public static final NodeClass<DynamicCounterNode> TYPE = NodeClass.create(DynamicCounterNode.class);
    @Input ValueNode increment;

    protected final String group;
    protected final String name;
    protected final boolean withContext;

    public DynamicCounterNode(String group, String name, ValueNode increment, boolean withContext) {
        this(TYPE, group, name, increment, withContext);
    }

    public static final long MIN_INCREMENT = 0;
    public static final long MAX_INCREMENT = 10_000;

    /**
     * Clamps {@code value} to a value between {@link #MIN_INCREMENT} and {@link #MAX_INCREMENT}.
     * This mitigates the possibility of overflowing benchmark counters.
     */
    public static long clampIncrement(long value) {
        return Math.min(Math.max(value, MIN_INCREMENT), MAX_INCREMENT);
    }

    private boolean checkIncrement() {
        if (increment.isJavaConstant()) {
            long incValue = increment.asJavaConstant().asLong();
            if (incValue < MIN_INCREMENT || incValue > MAX_INCREMENT) {
                String message = String.format("Benchmark counter %s:%s has increment out of range [%d .. %d]: %d", group, getNameWithContext(), MIN_INCREMENT, MAX_INCREMENT, incValue);
                assert false : message;
            }
        }
        return true;
    }

    protected DynamicCounterNode(NodeClass<? extends DynamicCounterNode> c, String group, String name, ValueNode increment, boolean withContext) {
        super(c, StampFactory.forVoid());
        this.group = group;
        this.name = name;
        this.increment = increment;
        this.withContext = withContext;
        assert checkIncrement();
    }

    public ValueNode getIncrement() {
        return increment;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public boolean isWithContext() {
        return withContext;
    }

    public static void addCounterBefore(String group, String name, long increment, boolean withContext, FixedNode position) {
        StructuredGraph graph = position.graph();
        graph.addBeforeFixed(position, position.graph().add(new DynamicCounterNode(group, name, ConstantNode.forLong(increment, position.graph()), withContext)));
    }

    @NodeIntrinsic
    public static native void counter(@ConstantNodeParameter String group, @ConstantNodeParameter String name, long increment, @ConstantNodeParameter boolean addContext);

    @Override
    public void generate(NodeLIRBuilderTool generator) {
        LIRGeneratorTool lirGen = generator.getLIRGeneratorTool();
        String nameWithContext = getNameWithContext();
        LIRInstruction counterOp = lirGen.createBenchmarkCounter(nameWithContext, getGroup(), generator.operand(increment));
        if (counterOp != null) {
            lirGen.append(counterOp);
        } else {
            throw GraalError.unimplemented("Benchmark counters not enabled or not implemented by the back end.");
        }
    }

    private String getNameWithContext() {
        String nameWithContext;
        if (isWithContext()) {
            nameWithContext = getName() + " @ ";
            if (graph().method() != null) {
                StackTraceElement stackTraceElement = graph().method().asStackTraceElement(0);
                if (stackTraceElement != null) {
                    nameWithContext += " " + stackTraceElement.toString();
                } else {
                    nameWithContext += graph().method().format("%h.%n");
                }
            }
            if (graph().name != null) {
                nameWithContext += " (" + graph().name + ")";
            }

        } else {
            nameWithContext = getName();
        }
        return nameWithContext;
    }
}
