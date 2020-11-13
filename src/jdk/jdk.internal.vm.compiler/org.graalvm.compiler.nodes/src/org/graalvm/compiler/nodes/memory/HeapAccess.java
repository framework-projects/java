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


package org.graalvm.compiler.nodes.memory;

/**
 * Encapsulates properties of a node describing how it accesses the heap.
 */
public interface HeapAccess {

    /**
     * The types of (write/read) barriers attached to stores.
     */
    enum BarrierType {
        /**
         * Primitive access which do not necessitate barriers.
         */
        NONE,
        /**
         * Array object access.
         */
        ARRAY,
        /**
         * Field object access.
         */
        FIELD,
        /**
         * Unknown (aka field or array) object access.
         */
        UNKNOWN,
        /**
         * Weak field access (e.g. Hotspot's Reference.referent field).
         */
        WEAK_FIELD
    }

    /**
     * Gets the write barrier type for that particular access.
     */
    BarrierType getBarrierType();
}
