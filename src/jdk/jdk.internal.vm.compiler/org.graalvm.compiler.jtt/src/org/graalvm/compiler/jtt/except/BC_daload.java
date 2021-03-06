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
/*
 */


package org.graalvm.compiler.jtt.except;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

public class BC_daload extends JTTTest {

    static double[] arr = {0.0, -1.1, 4.32, 6.06};

    public static double test(int arg) {
        final double[] array = arg == -2 ? null : arr;
        return array[arg];
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", -2);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", -1);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 0);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", 4);
    }

}
