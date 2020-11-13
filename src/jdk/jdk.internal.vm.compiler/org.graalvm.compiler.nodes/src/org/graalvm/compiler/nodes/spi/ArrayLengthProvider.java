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


package org.graalvm.compiler.nodes.spi;

import jdk.vm.ci.meta.ConstantReflectionProvider;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.ValuePhiNode;
import org.graalvm.compiler.nodes.ValueProxyNode;
import org.graalvm.compiler.nodes.util.GraphUtil;

public interface ArrayLengthProvider {

    /**
     * The different modes that determine what the results of {@link GraphUtil#arrayLength} and
     * {@link ArrayLengthProvider#findLength} can be used for.
     */
    enum FindLengthMode {
        /**
         * Use the result of {@link GraphUtil#arrayLength} and
         * {@link ArrayLengthProvider#findLength} to replace the explicit load of the array length
         * with a node that does not involve a memory access of the array length.
         *
         * Values that are defined inside a loop and flow out the loop need to be proxied by
         * {@link ValueProxyNode}. When this mode is used, new necessary proxy nodes are created
         * base on the proxies that were found while traversing the path to the length node. In
         * addition, new {@link ValuePhiNode phi nodes} can be created. The caller is responsible
         * for adding these nodes to the graph, i.e., the return value can be a node that is not yet
         * added to the graph.
         */
        CANONICALIZE_READ,

        /**
         * Use the result of {@link GraphUtil#arrayLength} and
         * {@link ArrayLengthProvider#findLength} only for decisions whether a certain optimization
         * is possible. No new nodes are created during the search, i.e., the result is either a
         * node that is already in the graph, or null.
         */
        SEARCH_ONLY
    }

    /**
     * Returns the length of the array described by this node, or null if it is not available.
     * Details of the different modes are documented in {@link FindLengthMode}.
     *
     * This method should not be called directly. Use {@link GraphUtil#arrayLength} instead.
     */
    ValueNode findLength(FindLengthMode mode, ConstantReflectionProvider constantReflection);
}
