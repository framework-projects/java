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
public class Catch_NPE_09 extends JTTTest {

    public static int test(int a) {
        int r = 0;
        try {
            r = 0;
            throwNPE(a);
            r = 1;
            throwNPE(a - 1);
        } catch (NullPointerException e) {
            return r + 10;
        }
        return r;
    }

    private static void throwNPE(int a) {
        if (a == 0) {
            throw null;
        }
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
