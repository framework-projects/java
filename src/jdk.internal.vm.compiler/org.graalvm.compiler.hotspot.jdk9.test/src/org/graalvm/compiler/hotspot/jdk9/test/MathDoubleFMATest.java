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


package org.graalvm.compiler.hotspot.jdk9.test;

import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.graalvm.compiler.api.test.Graal;
import org.graalvm.compiler.core.test.GraalCompilerTest;
import org.graalvm.compiler.hotspot.HotSpotGraalRuntimeProvider;
import org.graalvm.compiler.runtime.RuntimeProvider;
import org.graalvm.compiler.test.AddExports;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import jdk.vm.ci.amd64.AMD64;

@AddExports({"java.base/java.lang"})
@RunWith(Parameterized.class)
public final class MathDoubleFMATest extends GraalCompilerTest {

    @Before
    public void checkAMD64() {
        assumeTrue("skipping AMD64 specific test", getTarget().arch instanceof AMD64);
        HotSpotGraalRuntimeProvider rt = (HotSpotGraalRuntimeProvider) Graal.getRequiredCapability(RuntimeProvider.class);
        assumeTrue("skipping FMA specific test", rt.getVMConfig().useFMAIntrinsics);
    }

    @Parameters(name = "{0}, {1}, {2}")
    public static Collection<Object[]> data() {
        double[] inputs = {0.0d, 1.0d, 4.0d, -0.0d, -1.0d, -4.0d, Double.MIN_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                        Double.NaN, Double.longBitsToDouble(0xfff0000000000001L)};

        List<Object[]> tests = new ArrayList<>();
        for (double a : inputs) {
            for (double b : inputs) {
                for (double c : inputs) {
                    tests.add(new Object[]{a, b, c});
                }
            }
        }
        return tests;
    }

    @Parameter(value = 0) public double input0;
    @Parameter(value = 1) public double input1;
    @Parameter(value = 2) public double input2;

    public static double fma(double a, double b, double c) {
        return Math.fma(a, b, c);
    }

    @Test
    public void testFMA() {
        test("fma", input0, input1, input2);
    }

}
