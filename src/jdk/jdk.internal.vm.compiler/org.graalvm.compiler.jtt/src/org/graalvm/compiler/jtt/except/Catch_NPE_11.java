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


package org.graalvm.compiler.jtt.except;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class Catch_NPE_11 extends JTTTest {

    public static int test(int a) {
        int r = 0;
        try {
            r = 0;
            throwE(a);
            r = 1;
            throwE(a - 1);
        } catch (ArithmeticException e) {
            return r + 10;
        }
        return r;
    }

    private static int throwE(int a) {
        return 1 / a;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 2);
    }

}
