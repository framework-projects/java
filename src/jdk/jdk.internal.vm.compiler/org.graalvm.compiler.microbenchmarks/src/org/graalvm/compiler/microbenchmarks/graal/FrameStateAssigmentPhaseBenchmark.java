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

import org.graalvm.compiler.microbenchmarks.graal.util.FrameStateAssignmentState;
import org.graalvm.compiler.microbenchmarks.graal.util.MethodSpec;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Warmup;

import java.util.StringTokenizer;

@Warmup(iterations = 15)
public class FrameStateAssigmentPhaseBenchmark extends GraalBenchmark {

    @MethodSpec(declaringClass = StringTokenizer.class, name = "nextToken", parameters = {String.class})
    public static class StringTokenizedNextToken extends FrameStateAssignmentState {
    }

    @Benchmark
    public void nextToken(StringTokenizedNextToken s) {
        s.phase.apply(s.graph);
    }
}
