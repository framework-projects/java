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
 * Tests constant folding of float conversions
 */
public class Fold_Convert04 extends JTTTest {

    public static double test(double arg) {
        if (arg == 0) {
            return l2d();
        }
        if (arg == 1) {
            return f2d();
        }
        return 0;
    }

    public static double l2d() {
        long x = 1024;
        return x;
    }

    public static double f2d() {
        float x = -1.25f;
        return x;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0.0D);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1.0D);
    }

}
