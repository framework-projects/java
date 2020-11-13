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


package micro.benchmarks;

import org.openjdk.jmh.annotations.*;

/**
 * Benchmarks cost of ArrayList.
 */
public class ArrayAllocationBenchmark extends BenchmarkBase {

    @State(Scope.Benchmark)
    public static class ThreadState {
        @Param({"128", "256", "512", "1024", "2048", "4096", "8192", "16384", "32768", "65536", "131072"}) int size;
        byte[] result;
    }

    @Benchmark
    @Threads(8)
    @Warmup(iterations = 10)
    public void arrayAllocate(ThreadState state) {
        state.result = new byte[state.size];
    }
}
