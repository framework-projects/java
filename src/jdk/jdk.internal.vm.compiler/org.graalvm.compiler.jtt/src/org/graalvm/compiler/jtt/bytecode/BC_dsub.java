/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.jtt.bytecode;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class BC_dsub extends JTTTest {

    public static double test(double a, double b) {
        return a - b;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0.0d, 0.0d);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1.0d, 1.0d);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 253.11d, 54.43d);
    }

}
