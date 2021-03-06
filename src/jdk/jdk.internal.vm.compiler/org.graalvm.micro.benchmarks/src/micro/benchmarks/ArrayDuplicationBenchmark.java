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


package micro.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;

@State(Scope.Thread)
public class ArrayDuplicationBenchmark extends BenchmarkBase {

    /** How large should the test-arrays be. */
    private static final int TESTSIZE = 300;

    private Object[][] testObjectArray;

    private Object[][] testStringArray;

    private Object[] dummy;

    @Setup
    public void setup() {
        testObjectArray = new Object[TESTSIZE][];
        testStringArray = new Object[TESTSIZE][];
        for (int i = 0; i < TESTSIZE; i++) {
            testObjectArray[i] = new Object[20];
            testStringArray[i] = new String[200];
        }
    }

    @Setup(Level.Iteration)
    public void iterationSetup() {
        dummy = new Object[TESTSIZE * 3];
    }

    @TearDown(Level.Iteration)
    public void iterationTearDown() {
        dummy = null;
    }

    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public Object[] normalArraycopy() {
        int j = 0;
        for (int i = 0; i < TESTSIZE; i++) {
            dummy[j++] = normalArraycopy(testObjectArray[i]);
        }
        return dummy;
    }

    public Object[] normalArraycopy(Object[] cache) {
        Object[] result = new Object[cache.length];
        System.arraycopy(cache, 0, result, 0, result.length);
        return result;
    }

    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public Object[] arraysCopyOf() {
        int j = 0;
        for (int i = 0; i < TESTSIZE; i++) {
            dummy[j++] = arraysCopyOf(testObjectArray[i]);
        }
        return dummy;
    }

    public Object[] arraysCopyOf(Object[] cache) {
        return Arrays.copyOf(cache, cache.length);
    }

    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public Object[] arraysCopyOfToString() {
        int j = 0;
        for (int i = 0; i < TESTSIZE; i++) {
            dummy[j++] = arraysCopyOfToString(testStringArray[i]);
        }
        return dummy;
    }

    public Object[] arraysCopyOfToString(Object[] cache) {
        return Arrays.copyOf(cache, cache.length, String[].class);
    }

    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public Object[] cloneObjectArray() {
        int j = 0;
        for (int i = 0; i < TESTSIZE; i++) {
            dummy[j++] = arraysClone(testObjectArray[i]);
        }
        return dummy;
    }

    @SuppressWarnings("cast")
    public Object[] arraysClone(Object[] cache) {
        return (Object[]) cache.clone();
    }

}
