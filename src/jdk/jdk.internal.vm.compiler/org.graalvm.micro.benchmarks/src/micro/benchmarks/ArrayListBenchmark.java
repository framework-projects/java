/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;

/**
 * Benchmarks cost of ArrayList.
 */
public class ArrayListBenchmark extends BenchmarkBase {

    private static final int N = 100;

    @State(Scope.Benchmark)
    public static class ThreadState {
        final ArrayList<Integer> list = new ArrayList<>(N);
    }

    @Benchmark
    @Warmup(iterations = 20)
    public void addBoxedAndClear(ThreadState state) {
        for (int i = 0; i < N; ++i) {
            state.list.add(i);
        }
        state.list.clear();
    }

    @Benchmark
    @Warmup(iterations = 20)
    public void addNullAndClear(ThreadState state) {
        for (int i = 0; i < N; ++i) {
            state.list.add(null);
        }
        state.list.clear();
    }

    @State(Scope.Benchmark)
    public static class ClearedThreadState {
        final ArrayList<Integer> list = new ArrayList<>(N);

        // We don't want to measure the cost of list clearing
        @Setup(Level.Invocation)
        public void beforeInvocation() {
            list.clear();
        }
    }

    @Benchmark
    @Warmup(iterations = 20)
    public void addNull(ClearedThreadState state) {
        for (int i = 0; i < N; ++i) {
            state.list.add(null);
        }
    }
}
