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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

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
