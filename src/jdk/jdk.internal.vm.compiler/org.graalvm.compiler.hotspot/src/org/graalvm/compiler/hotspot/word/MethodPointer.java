/*
 * Copyright (c) 2014, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.word;

import jdk.internal.vm.compiler.word.Pointer;

import static org.graalvm.compiler.hotspot.word.HotSpotOperation.HotspotOpcode.*;

/**
 * Marker type for a metaspace pointer to a method.
 */
public abstract class MethodPointer extends MetaspacePointer {

    @HotSpotOperation(opcode = POINTER_EQ)
    public abstract boolean equal(KlassPointer other);

    @HotSpotOperation(opcode = POINTER_NE)
    public abstract boolean notEqual(KlassPointer other);

    @HotSpotOperation(opcode = TO_METHOD_POINTER)
    public static native MethodPointer fromWord(Pointer pointer);
}
