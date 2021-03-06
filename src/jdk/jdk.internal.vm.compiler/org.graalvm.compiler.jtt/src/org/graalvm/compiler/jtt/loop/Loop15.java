/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.jtt.loop;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

public class Loop15 extends JTTTest {

    public static int test(int arg) {
        Object o = null;
        int result = 10;
        for (int k = 0; k < arg; ++k) {
            if (o == null) {
                o = new Object();
            }
            if (k >= 5) {
                break;
            }
            result++;
        }
        return result + (o == null ? 0 : 1);
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 5);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 0);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 1);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", 10);
    }

}
