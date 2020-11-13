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


package org.graalvm.compiler.jtt.optimize;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 * Tests optimization of float operations.
 */
public class VN_Double03 extends JTTTest {

    public static double test(double arg) {
        if (arg == -0.0d) {
            return arg;
        }
        return 0;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", -0.0d);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 0.0d);
    }
}
