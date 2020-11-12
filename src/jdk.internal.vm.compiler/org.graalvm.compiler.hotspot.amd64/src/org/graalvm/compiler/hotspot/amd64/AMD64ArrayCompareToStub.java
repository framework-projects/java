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
import org.graalvm.compiler.replacements.nodes.ArrayCompareToNode;
import jdk.internal.vm.compiler.word.Pointer;

import jdk.vm.ci.meta.JavaKind;

public final class AMD64ArrayCompareToStub extends SnippetStub {

    public static final ForeignCallDescriptor STUB_BYTE_ARRAY_COMPARE_TO_BYTE_ARRAY = new ForeignCallDescriptor(
                    "byteArrayCompareToByteArray", int.class, Pointer.class, Pointer.class, int.class, int.class);
    public static final ForeignCallDescriptor STUB_BYTE_ARRAY_COMPARE_TO_CHAR_ARRAY = new ForeignCallDescriptor(
                    "byteArrayCompareToCharArray", int.class, Pointer.class, Pointer.class, int.class, int.class);
    public static final ForeignCallDescriptor STUB_CHAR_ARRAY_COMPARE_TO_BYTE_ARRAY = new ForeignCallDescriptor(
                    "charArrayCompareToByteArray", int.class, Pointer.class, Pointer.class, int.class, int.class);
    public static final ForeignCallDescriptor STUB_CHAR_ARRAY_COMPARE_TO_CHAR_ARRAY = new ForeignCallDescriptor(
                    "charArrayCompareToCharArray", int.class, Pointer.class, Pointer.class, int.class, int.class);

    public AMD64ArrayCompareToStub(ForeignCallDescriptor foreignCallDescriptor, OptionValues options, HotSpotProviders providers, HotSpotForeignCallLinkage linkage) {
        super(foreignCallDescriptor.getName(), options, providers, linkage);
    }

    @Snippet
    private static int byteArrayCompareToByteArray(Pointer array1, Pointer array2, int length1, int length2) {
        return ArrayCompareToNode.compareTo(array1, array2, length1, length2, JavaKind.Byte, JavaKind.Byte);
    }

    @Snippet
    private static int byteArrayCompareToCharArray(Pointer array1, Pointer array2, int length1, int length2) {
        return ArrayCompareToNode.compareTo(array1, array2, length1, length2, JavaKind.Byte, JavaKind.Char);
    }

    @Snippet
    private static int charArrayCompareToByteArray(Pointer array1, Pointer array2, int length1, int length2) {
        return ArrayCompareToNode.compareTo(array1, array2, length1, length2, JavaKind.Char, JavaKind.Byte);
    }

    @Snippet
    private static int charArrayCompareToCharArray(Pointer array1, Pointer array2, int length1, int length2) {
        return ArrayCompareToNode.compareTo(array1, array2, length1, length2, JavaKind.Char, JavaKind.Char);
    }
}
