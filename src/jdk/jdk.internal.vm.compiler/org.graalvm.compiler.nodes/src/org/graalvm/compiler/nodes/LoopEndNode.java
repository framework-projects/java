/*
 * Copyright (c) 2011, 2018, Oracle and/or its affiliates. All rights reserved.
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

import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeCycles;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodeinfo.NodeSize;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;

import java.util.Collections;

import static org.graalvm.compiler.nodeinfo.InputType.Association;
import static org.graalvm.compiler.nodeinfo.NodeCycles.*;
import static org.graalvm.compiler.nodeinfo.NodeSize.*;

/**
 * LoopEnd nodes represent a loop back-edge. When a LoopEnd is reached, execution continues at the
 * {@linkplain #loopBegin() loop header}.
 */
@NodeInfo(cycles = CYCLES_1, cyclesRationale = "Backedge jmp", size = SIZE_1, sizeRationale = "Backedge jmp")
public final class LoopEndNode extends AbstractEndNode {

    public static final NodeClass<LoopEndNode> TYPE = NodeClass.create(LoopEndNode.class);

    /*
     * The declared type of the field cannot be LoopBeginNode, because loop explosion during partial
     * evaluation can temporarily assign a non-loop begin. This node will then be deleted shortly
     * after - but we still must not have type system violations for that short amount of time.
     */
    @Input(Association) AbstractBeginNode loopBegin;
    protected int endIndex;

    /**
     * Most loop ends need a safepoint (flag set to true) so that garbage collection can interrupt a
     * long-running (possibly endless) loop. Safepoints may be disabled for two reasons: 1) Some
     * code must be safepoint free, i.e., uninterruptible by garbage collection. 2) An optimization
     * phase determined that the loop already has another safepoint or cannot be endless, so there
     * is no need for a loop-end safepoint.
     *
     * Note that 1) is a hard correctness issue: emitting a safepoint in uninterruptible code is a
     * bug, i.e., it is not allowed to set the flag back to true once it is false. To ensure that
     * loop ends that are created late, e.g., during control flow simplifications, have no
     * safepoints in such cases, the safepoints are actually disabled for the
     * {@link LoopBeginNode#canEndsSafepoint loop begin}. New loop ends inherit the flag value from
     * the loop begin.
     */
    boolean canSafepoint;

    public LoopEndNode(LoopBeginNode begin) {
        super(TYPE);
        int idx = begin.nextEndIndex();
        assert idx >= 0;
        this.endIndex = idx;
        this.loopBegin = begin;
        this.canSafepoint = begin.canEndsSafepoint;
    }

    @Override
    public AbstractMergeNode merge() {
        return loopBegin();
    }

    public LoopBeginNode loopBegin() {
        return (LoopBeginNode) loopBegin;
    }

    public void setLoopBegin(LoopBeginNode x) {
        updateUsages(this.loopBegin, x);
        this.loopBegin = x;
    }

    /**
     * Disables safepoints for only this loop end (in contrast to disabling it for
     * {@link LoopBeginNode#disableSafepoint() the whole loop}.
     */
    public void disableSafepoint() {
        this.canSafepoint = false;
    }

    public boolean canSafepoint() {
        assert !canSafepoint || loopBegin().canEndsSafepoint : "When safepoints are disabled for loop begin, safepoints must be disabled for all loop ends";
        return canSafepoint;
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        gen.visitLoopEnd(this);
        super.generate(gen);
    }

    @Override
    public boolean verify() {
        assertTrue(loopBegin != null, "must have a loop begin");
        assertTrue(hasNoUsages(), "LoopEnds can not be used");
        return super.verify();
    }

    /**
     * Returns the index of this loop end amongst its {@link LoopBeginNode}'s loop ends.<br>
     *
     * Since a LoopBeginNode also has {@linkplain LoopBeginNode#forwardEnds() forward ends}, this is
     * <b>not</b> the index into {@link PhiNode} values at the loop begin. Use
     * {@link LoopBeginNode#phiPredecessorIndex(AbstractEndNode)} for this purpose.
     *
     */
    int endIndex() {
        return endIndex;
    }

    void setEndIndex(int idx) {
        this.endIndex = idx;
    }

    @Override
    public Iterable<? extends Node> cfgSuccessors() {
        return Collections.emptyList();
    }

    @Override
    public NodeCycles estimatedNodeCycles() {
        if (loopBegin() == null) {
            return CYCLES_UNKNOWN;
        }
        if (canSafepoint()) {
            // jmp+read
            return CYCLES_2;
        }
        return super.estimatedNodeCycles();
    }

    @Override
    public NodeSize estimatedNodeSize() {
        if (loopBegin() == null) {
            return SIZE_UNKNOWN;
        }
        if (canSafepoint()) {
            return SIZE_2;
        }
        return super.estimatedNodeSize();
    }
}
