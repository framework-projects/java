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


















package jdk.internal.vm.compiler.word;

/**
 * Marker interface for all {@link WordBase word types} that have the semantic of a pointer (but not
 * necessarily all the memory access methods defined in {@link Pointer}).
 *
 * @since 19.0
 */
public interface PointerBase extends ComparableWord {

    /**
     * Returns true if this pointer is the {@link WordFactory#nullPointer null pointer}.
     *
     * @since 19.0
     */
    boolean isNull();

    /**
     * Returns true if this pointer is not the {@link WordFactory#nullPointer null pointer}.
     *
     * @since 19.0
     */
    boolean isNonNull();
}
