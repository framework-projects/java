/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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
import org.graalvm.compiler.replacements.amd64.AMD64ArrayIndexOfNode;

public class AMD64ArrayIndexOfStub extends SnippetStub {

    public AMD64ArrayIndexOfStub(ForeignCallDescriptor foreignCallDescriptor, OptionValues options, HotSpotProviders providers, HotSpotForeignCallLinkage linkage) {
        super(foreignCallDescriptor.getName(), options, providers, linkage);
    }

    @Snippet
    private static int indexOfTwoConsecutiveBytes(byte[] array, int arrayLength, int fromIndex, int searchValue) {
        return AMD64ArrayIndexOfNode.indexOf2ConsecutiveBytes(array, arrayLength, fromIndex, searchValue);
    }

    @Snippet
    private static int indexOfTwoConsecutiveChars(char[] array, int arrayLength, int fromIndex, int searchValue) {
        return AMD64ArrayIndexOfNode.indexOf2ConsecutiveChars(array, arrayLength, fromIndex, searchValue);
    }

    @Snippet
    private static int indexOfTwoConsecutiveCharsCompact(byte[] array, int arrayLength, int fromIndex, int searchValue) {
        return AMD64ArrayIndexOfNode.indexOf2ConsecutiveChars(array, arrayLength, fromIndex, searchValue);
    }

    @Snippet
    private static int indexOf1Byte(byte[] array, int arrayLength, int fromIndex, byte b) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, b);
    }

    @Snippet
    private static int indexOf2Bytes(byte[] array, int arrayLength, int fromIndex, byte b1, byte b2) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, b1, b2);
    }

    @Snippet
    private static int indexOf3Bytes(byte[] array, int arrayLength, int fromIndex, byte b1, byte b2, byte b3) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, b1, b2, b3);
    }

    @Snippet
    private static int indexOf4Bytes(byte[] array, int arrayLength, int fromIndex, byte b1, byte b2, byte b3, byte b4) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, b1, b2, b3, b4);
    }

    @Snippet
    private static int indexOf1Char(char[] array, int arrayLength, int fromIndex, char c) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, c);
    }

    @Snippet
    private static int indexOf2Chars(char[] array, int arrayLength, int fromIndex, char c1, char c2) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, c1, c2);
    }

    @Snippet
    private static int indexOf3Chars(char[] array, int arrayLength, int fromIndex, char c1, char c2, char c3) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, c1, c2, c3);
    }

    @Snippet
    private static int indexOf4Chars(char[] array, int arrayLength, int fromIndex, char c1, char c2, char c3, char c4) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, c1, c2, c3, c4);
    }

    @Snippet
    private static int indexOf1CharCompact(byte[] array, int arrayLength, int fromIndex, char c) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, c);
    }

    @Snippet
    private static int indexOf2CharsCompact(byte[] array, int arrayLength, int fromIndex, char c1, char c2) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, c1, c2);
    }

    @Snippet
    private static int indexOf3CharsCompact(byte[] array, int arrayLength, int fromIndex, char c1, char c2, char c3) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, c1, c2, c3);
    }

    @Snippet
    private static int indexOf4CharsCompact(byte[] array, int arrayLength, int fromIndex, char c1, char c2, char c3, char c4) {
        return AMD64ArrayIndexOfNode.indexOf(array, arrayLength, fromIndex, c1, c2, c3, c4);
    }
}
