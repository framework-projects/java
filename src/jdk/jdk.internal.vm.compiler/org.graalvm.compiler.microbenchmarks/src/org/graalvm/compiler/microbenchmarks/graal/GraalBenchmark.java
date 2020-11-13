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


package org.graalvm.compiler.microbenchmarks.graal;

import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

import static org.graalvm.compiler.microbenchmarks.graal.GraalBenchmark.Defaults.*;

/**
 * All classes defining Graal benchmarks must subclass this class as it defines the default value
 * for each benchmark option. Individual options can be overridden in the subclasses or by an
 * individual benchmark.
 */
@Warmup(iterations = WARMUP_ITERATIONS)
@Measurement(iterations = MEASUREMENT_ITERATIONS)
@Fork(FORKS)
public class GraalBenchmark {

    public static class Defaults {
        public static final int MEASUREMENT_ITERATIONS = 5;
        public static final int WARMUP_ITERATIONS = 5;
        public static final int FORKS = 1;
    }
}
