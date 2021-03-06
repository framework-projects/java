/*
 * Copyright (c) 2009, 2018, Oracle and/or its affiliates. All rights reserved.
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

/*
 * Tests optimization of float operations.
 */
public class VN_Double01 extends JTTTest {

    public static double test(double arg) {
        if (arg == 0) {
            return add(arg + 10);
        }
        if (arg == 1) {
            return sub(arg + 10);
        }
        if (arg == 2) {
            return mul(arg + 10);
        }
        if (arg == 3) {
            return div(arg + 10);
        }
        return 0;
    }

    public static double add(double x) {
        double c = 1;
        double t = x + c;
        double u = x + c;
        return t + u;
    }

    public static double sub(double x) {
        double c = 1;
        double t = x - c;
        double u = x - c;
        return t - u;
    }

    public static double mul(double x) {
        double c = 1;
        double t = x * c;
        double u = x * c;
        return t * u;
    }

    public static double div(double x) {
        double c = 1;
        double t = x / c;
        double u = x / c;
        return t / u;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0d);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1d);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 2d);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", 3d);
    }

}
