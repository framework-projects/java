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


package org.graalvm.compiler.jtt.optimize;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * Tests comparison-based canonicalizations for floats.
 */
@RunWith(Parameterized.class)
public class TrichotomyFloats extends JTTTest {

    public static int test0(float x, float y) {
        return x < y ? -1 : (x == y ? 0 : 1);
    }

    public static int test1(float x, float y) {
        return x < y ? 1 : (x == y ? 0 : -1);
    }

    public static int test2(float x, float y) {
        return x == y ? 0 : (x < y ? -1 : 1);
    }

    public static int test3(float x, float y) {
        return x == y ? 0 : (x < y ? 1 : -1);
    }

    public static int test4(float x, float y) {
        return x == y ? 0 : (x > y ? -1 : 1);
    }

    public static int test5(float x, float y) {
        return x == y ? 0 : (x > y ? 1 : -1);
    }

    public static int test6(float x, float y) {
        return x < y ? 1 : (x > y ? -1 : 0);
    }

    public static int test7(float x, float y) {
        return x < y ? -1 : (x > y ? 1 : 0);
    }

    @Parameter(value = 0) public float x;
    @Parameter(value = 1) public float y;

    @Parameters(name = "x = {0}, y = {1}")
    public static Collection<Object[]> data() {
        List<Object[]> parameters = new ArrayList<>();
        float[] floats = {Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN, 0f, -1f, Float.MIN_VALUE, Float.MAX_VALUE};
        for (float f1 : floats) {
            for (float f2 : floats) {
                parameters.add(new Object[]{f1, f2});
            }
        }
        return parameters;
    }

    @Test
    public void run0() {
        runTest("test0", x, y);
    }

    @Test
    public void run1() {
        runTest("test1", x, y);
    }

    @Test
    public void run2() {
        runTest("test2", x, y);
    }

    @Test
    public void run3() {
        runTest("test3", x, y);
    }

    @Test
    public void run4() {
        runTest("test4", x, y);
    }

    @Test
    public void run5() {
        runTest("test5", x, y);
    }

    @Test
    public void run6() {
        runTest("test6", x, y);
    }

    @Test
    public void run7() {
        runTest("test7", x, y);
    }
}