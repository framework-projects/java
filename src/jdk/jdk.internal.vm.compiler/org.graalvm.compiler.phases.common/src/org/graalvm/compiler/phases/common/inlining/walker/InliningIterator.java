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


package org.graalvm.compiler.phases.common.inlining.walker;

import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeBitMap;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.java.MethodCallTargetNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Given a graph, visit all fixed nodes in dominator-based order, collecting in the process the
 * {@link Invoke} nodes with {@link MethodCallTargetNode}. Such list of callsites is returned by
 * {@link #apply()}
 */
public class InliningIterator {

    private final StartNode start;
    private final Deque<FixedNode> nodeQueue;
    private final NodeBitMap queuedNodes;

    public InliningIterator(StructuredGraph graph) {
        this.start = graph.start();
        this.nodeQueue = new ArrayDeque<>();
        this.queuedNodes = graph.createNodeBitMap();
        assert start.isAlive();
    }

    public LinkedList<Invoke> apply() {
        LinkedList<Invoke> invokes = new LinkedList<>();
        FixedNode current;
        forcedQueue(start);

        while ((current = nextQueuedNode()) != null) {
            assert current.isAlive();

            if (current instanceof Invoke && ((Invoke) current).callTarget() instanceof MethodCallTargetNode) {
                if (current != start) {
                    invokes.addLast((Invoke) current);
                }
                queueSuccessors(current);
            } else if (current instanceof LoopBeginNode) {
                queueSuccessors(current);
            } else if (current instanceof LoopEndNode) {
                // nothing to do
            } else if (current instanceof AbstractMergeNode) {
                queueSuccessors(current);
            } else if (current instanceof FixedWithNextNode) {
                queueSuccessors(current);
            } else if (current instanceof EndNode) {
                queueMerge((EndNode) current);
            } else if (current instanceof ControlSinkNode) {
                // nothing to do
            } else if (current instanceof ControlSplitNode) {
                queueSuccessors(current);
            } else {
                assert false : current;
            }
        }

        assert invokes.size() == count(start.graph().getInvokes());
        return invokes;
    }

    private void queueSuccessors(FixedNode x) {
        for (Node node : x.successors()) {
            queue(node);
        }
    }

    private void queue(Node node) {
        if (node != null && !queuedNodes.isMarked(node)) {
            forcedQueue(node);
        }
    }

    private void forcedQueue(Node node) {
        queuedNodes.mark(node);
        nodeQueue.addFirst((FixedNode) node);
    }

    private FixedNode nextQueuedNode() {
        if (nodeQueue.isEmpty()) {
            return null;
        }

        FixedNode result = nodeQueue.removeFirst();
        assert queuedNodes.isMarked(result);
        return result;
    }

    private void queueMerge(AbstractEndNode end) {
        AbstractMergeNode merge = end.merge();
        if (!queuedNodes.isMarked(merge) && visitedAllEnds(merge)) {
            queuedNodes.mark(merge);
            nodeQueue.add(merge);
        }
    }

    private boolean visitedAllEnds(AbstractMergeNode merge) {
        for (int i = 0; i < merge.forwardEndCount(); i++) {
            if (!queuedNodes.isMarked(merge.forwardEndAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static int count(Iterable<Invoke> invokes) {
        int count = 0;
        Iterator<Invoke> iterator = invokes.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }
}
