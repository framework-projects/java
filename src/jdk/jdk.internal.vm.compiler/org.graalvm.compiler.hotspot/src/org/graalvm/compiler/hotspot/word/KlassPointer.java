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

import jdk.internal.vm.compiler.word.LocationIdentity;
import jdk.internal.vm.compiler.word.Pointer;
import org.graalvm.compiler.word.Word.Opcode;
import org.graalvm.compiler.word.Word.Operation;

import static org.graalvm.compiler.hotspot.word.HotSpotOperation.HotspotOpcode.*;

/**
 * Marker type for a metaspace pointer to a type.
 */
public abstract class KlassPointer extends MetaspacePointer {

    @HotSpotOperation(opcode = POINTER_EQ)
    public abstract boolean equal(KlassPointer other);

    @HotSpotOperation(opcode = POINTER_NE)
    public abstract boolean notEqual(KlassPointer other);

    @HotSpotOperation(opcode = TO_KLASS_POINTER)
    public static native KlassPointer fromWord(Pointer pointer);

    @HotSpotOperation(opcode = READ_KLASS_POINTER)
    public native KlassPointer readKlassPointer(int offset, LocationIdentity locationIdentity);

    @Operation(opcode = Opcode.WRITE_POINTER)
    public native void writeKlassPointer(int offset, KlassPointer t, LocationIdentity locationIdentity);
}
