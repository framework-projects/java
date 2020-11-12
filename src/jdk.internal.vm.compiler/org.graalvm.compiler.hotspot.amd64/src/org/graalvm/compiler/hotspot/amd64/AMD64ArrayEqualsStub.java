/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.amd64;

import org.graalvm.compiler.api.replacements.Snippet;
import org.graalvm.compiler.core.common.spi.ForeignCallDescriptor;
import org.graalvm.compiler.hotspot.HotSpotForeignCallLinkage;
import org.graalvm.compiler.hotspot.meta.HotSpotProviders;
import org.graalvm.compiler.hotspot.stubs.SnippetStub;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.replacements.nodes.ArrayEqualsNode;
import org.graalvm.compiler.replacements.nodes.ArrayRegionEqualsNode;
import jdk.internal.vm.compiler.word.Pointer;

import jdk.vm.ci.meta.JavaKind;

public final class AMD64ArrayEqualsStub extends SnippetStub {

    public static final ForeignCallDescriptor STUB_BOOLEAN_ARRAY_EQUALS = new ForeignCallDescriptor(
                    "booleanArraysEquals", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_BYTE_ARRAY_EQUALS = new ForeignCallDescriptor(
                    "byteArraysEquals", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_CHAR_ARRAY_EQUALS = new ForeignCallDescriptor(
                    "charArraysEquals", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_SHORT_ARRAY_EQUALS = new ForeignCallDescriptor(
                    "shortArraysEquals", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_INT_ARRAY_EQUALS = new ForeignCallDescriptor(
                    "intArraysEquals", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_LONG_ARRAY_EQUALS = new ForeignCallDescriptor(
                    "longArraysEquals", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_FLOAT_ARRAY_EQUALS = new ForeignCallDescriptor(
                    "floatArraysEquals", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_DOUBLE_ARRAY_EQUALS = new ForeignCallDescriptor(
                    "doubleArraysEquals", boolean.class, Pointer.class, Pointer.class, int.class);

    public static final ForeignCallDescriptor STUB_BYTE_ARRAY_EQUALS_DIRECT = new ForeignCallDescriptor(
                    "byteArraysEqualsDirect", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_CHAR_ARRAY_EQUALS_DIRECT = new ForeignCallDescriptor(
                    "charArraysEqualsDirect", boolean.class, Pointer.class, Pointer.class, int.class);
    public static final ForeignCallDescriptor STUB_CHAR_ARRAY_EQUALS_BYTE_ARRAY = new ForeignCallDescriptor(
                    "charArrayEqualsByteArray", boolean.class, Pointer.class, Pointer.class, int.class);

    public AMD64ArrayEqualsStub(ForeignCallDescriptor foreignCallDescriptor, OptionValues options, HotSpotProviders providers, HotSpotForeignCallLinkage linkage) {
        super(foreignCallDescriptor.getName(), options, providers, linkage);
    }

    @Snippet
    private static boolean booleanArraysEquals(Pointer array1, Pointer array2, int length) {
        return ArrayEqualsNode.equals(array1, array2, length, JavaKind.Boolean);
    }

    @Snippet
    private static boolean byteArraysEquals(Pointer array1, Pointer array2, int length) {
        return ArrayEqualsNode.equals(array1, array2, length, JavaKind.Byte);
    }

    @Snippet
    private static boolean charArraysEquals(Pointer array1, Pointer array2, int length) {
        return ArrayEqualsNode.equals(array1, array2, length, JavaKind.Char);
    }

    @Snippet
    private static boolean shortArraysEquals(Pointer array1, Pointer array2, int length) {
        return ArrayEqualsNode.equals(array1, array2, length, JavaKind.Short);
    }

    @Snippet
    private static boolean intArraysEquals(Pointer array1, Pointer array2, int length) {
        return ArrayEqualsNode.equals(array1, array2, length, JavaKind.Int);
    }

    @Snippet
    private static boolean longArraysEquals(Pointer array1, Pointer array2, int length) {
        return ArrayEqualsNode.equals(array1, array2, length, JavaKind.Long);
    }

    @Snippet
    private static boolean floatArraysEquals(Pointer array1, Pointer array2, int length) {
        return ArrayEqualsNode.equals(array1, array2, length, JavaKind.Float);
    }

    @Snippet
    private static boolean doubleArraysEquals(Pointer array1, Pointer array2, int length) {
        return ArrayEqualsNode.equals(array1, array2, length, JavaKind.Double);
    }

    @Snippet
    private static boolean byteArraysEqualsDirect(Pointer array1, Pointer array2, int length) {
        return ArrayRegionEqualsNode.regionEquals(array1, array2, length, JavaKind.Byte, JavaKind.Byte);
    }

    @Snippet
    private static boolean charArraysEqualsDirect(Pointer array1, Pointer array2, int length) {
        return ArrayRegionEqualsNode.regionEquals(array1, array2, length, JavaKind.Char, JavaKind.Char);
    }

    @Snippet
    private static boolean charArrayEqualsByteArray(Pointer array1, Pointer array2, int length) {
        return ArrayRegionEqualsNode.regionEquals(array1, array2, length, JavaKind.Char, JavaKind.Byte);
    }
}
