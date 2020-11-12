/*
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
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



package org.graalvm.compiler.core.test;

import java.util.Arrays;

import org.junit.Test;

import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.java.NewArrayNode;

public class CopyOfVirtualizationTest extends GraalCompilerTest {

    @Override
    protected void checkMidTierGraph(StructuredGraph graph) {
        assertTrue(graph.getNodes().filter(node -> node instanceof NewArrayNode).count() == 0, "shouldn't require allocation in %s", graph);
        super.checkMidTierGraph(graph);
    }

    public byte byteCopyOfVirtualization(int index) {
        byte[] array = new byte[]{1, 2, 3, 4};
        return Arrays.copyOf(array, array.length)[index];
    }

    public short shortCopyOfVirtualization(int index) {
        short[] array = new short[]{1, 2, 3, 4};
        return Arrays.copyOf(array, array.length)[index];
    }

    public char charCopyOfVirtualization(int index) {
        char[] array = new char[]{1, 2, 3, 4};
        return Arrays.copyOf(array, array.length)[index];
    }

    public int intCopyOfVirtualization(int index) {
        int[] array = new int[]{1, 2, 3, 4};
        return Arrays.copyOf(array, array.length)[index];
    }

    public long longCopyOfVirtualization(int index) {
        long[] array = new long[]{1, 2, 3, 4};
        return Arrays.copyOf(array, array.length)[index];
    }

    public float floatCopyOfVirtualization(int index) {
        float[] array = new float[]{1, 2, 3, 4};
        return Arrays.copyOf(array, array.length)[index];
    }

    public double doubleCopyOfVirtualization(int index) {
        double[] array = new double[]{1, 2, 3, 4};
        return Arrays.copyOf(array, array.length)[index];
    }

    public Object objectCopyOfVirtualization(int index) {
        Object[] array = new Object[]{1, 2, 3, 4};
        return Arrays.copyOf(array, array.length)[index];
    }

    // @Test
    public void testCopyOfVirtualization() {
        test("byteCopyOfVirtualization", 3);
        test("shortCopyOfVirtualization", 3);
        test("charCopyOfVirtualization", 3);
        test("intCopyOfVirtualization", 3);
        test("longCopyOfVirtualization", 3);
        test("floatCopyOfVirtualization", 3);
        test("doubleCopyOfVirtualization", 3);
        test("objectCopyOfVirtualization", 3);
    }

    static final byte[] byteArray = new byte[]{1, 2, 3, 4};

    public byte byteCopyOfVirtualizableAllocation() {
        return Arrays.copyOf(byteArray, byteArray.length)[3];
    }

    static final short[] shortArray = new short[]{1, 2, 3, 4};

    public short shortCopyOfVirtualizableAllocation() {
        return Arrays.copyOf(shortArray, shortArray.length)[3];
    }

    static final char[] charArray = new char[]{1, 2, 3, 4};

    public char charCopyOfVirtualizableAllocation() {
        return Arrays.copyOf(charArray, charArray.length)[3];
    }

    static final int[] intArray = new int[]{1, 2, 3, 4};

    public int intCopyOfVirtualizableAllocation() {
        return Arrays.copyOf(intArray, intArray.length)[3];
    }

    static final long[] longArray = new long[]{1, 2, 3, 4};

    public long longCopyOfVirtualizableAllocation() {
        return Arrays.copyOf(longArray, longArray.length)[3];
    }

    static final float[] floatArray = new float[]{1, 2, 3, 4};

    public float floatCopyOfVirtualizableAllocation() {
        return Arrays.copyOf(floatArray, floatArray.length)[3];
    }

    static final double[] doubleArray = new double[]{1, 2, 3, 4};

    public double doubleCopyOfVirtualizableAllocation() {
        return Arrays.copyOf(doubleArray, doubleArray.length)[3];
    }

    static final Object[] objectArray = new Object[]{1, 2, 3, 4};

    public Object objectCopyOfVirtualizableAllocation() {
        return Arrays.copyOf(objectArray, objectArray.length)[3];
    }

    @Test
    public void testCopyOfVirtualizableAllocation() {
        test("byteCopyOfVirtualizableAllocation");
        test("shortCopyOfVirtualizableAllocation");
        test("charCopyOfVirtualizableAllocation");
        test("intCopyOfVirtualizableAllocation");
        test("longCopyOfVirtualizableAllocation");
        test("floatCopyOfVirtualizableAllocation");
        test("doubleCopyOfVirtualizableAllocation");
        test("objectCopyOfVirtualizableAllocation");
    }
}