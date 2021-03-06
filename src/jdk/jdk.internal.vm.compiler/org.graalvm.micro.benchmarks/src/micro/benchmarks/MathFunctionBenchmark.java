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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;

/**
 * Benchmarks cost of Math intrinsics.
 */
public class MathFunctionBenchmark extends BenchmarkBase {

    @State(Scope.Benchmark)
    public static class ThreadState {
        double[] data = randomDoubles(100);
        double[] result = new double[100];
        double k = data[0];

        static double[] randomDoubles(int len) {
            double[] data = new double[len];
            Random r = new Random();
            for (int i = 0; i < data.length; i++) {
                data[i] = r.nextDouble();
            }
            return data;
        }
    }

    @Benchmark
    @Warmup(iterations = 5)
    public void mathLog(ThreadState state) {
        double[] data = state.data;
        for (int i = 0; i < data.length; i++) {
            double[] result = state.result;
            result[i] = Math.log(data[i]);
        }
    }

    @Benchmark
    @Warmup(iterations = 5)
    public void mathLog10(ThreadState state) {
        double[] data = state.data;
        for (int i = 0; i < data.length; i++) {
            double[] result = state.result;
            result[i] = Math.log10(data[i]);
        }
    }

    @Benchmark
    @Warmup(iterations = 5)
    public void mathSin(ThreadState state) {
        double[] data = state.data;
        for (int i = 0; i < data.length; i++) {
            double[] result = state.result;
            result[i] = Math.sin(data[i]);
        }
    }

    @Benchmark
    @Warmup(iterations = 5)
    public void mathCos(ThreadState state) {
        double[] data = state.data;
        for (int i = 0; i < data.length; i++) {
            double[] result = state.result;
            result[i] = Math.cos(data[i]);
        }
    }

    @Benchmark
    @Warmup(iterations = 5)
    public void mathTan(ThreadState state) {
        double[] data = state.data;
        for (int i = 0; i < data.length; i++) {
            double[] result = state.result;
            result[i] = Math.tan(data[i]);
        }
    }

    @Benchmark
    @Warmup(iterations = 1)
    public void mathSqrt(ThreadState state, Blackhole blackhole) {
        blackhole.consume(Math.sqrt(state.k));
    }

    @Benchmark
    @Warmup(iterations = 1)
    public void strictMathSqrt(ThreadState state, Blackhole blackhole) {
        blackhole.consume(StrictMath.sqrt(state.k));
    }
}
