/*
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.loop.phases;

import org.graalvm.compiler.loop.LoopPolicies;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.spi.CoreProviders;

public abstract class ContextlessLoopPhase<P extends LoopPolicies> extends LoopPhase<P> {

    public ContextlessLoopPhase(P policies) {
        super(policies);
    }

    public final void apply(final StructuredGraph graph) {
        apply(graph, true);
    }

    public final void apply(final StructuredGraph graph, final boolean dumpGraph) {
        apply(graph, null, dumpGraph);
    }

    protected abstract void run(StructuredGraph graph);

    @Override
    protected final void run(StructuredGraph graph, CoreProviders context) {
        run(graph);
    }
}
