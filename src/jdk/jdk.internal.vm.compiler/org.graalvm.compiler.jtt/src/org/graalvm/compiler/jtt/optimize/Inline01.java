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
 */
public class Inline01 extends JTTTest {

    public static int test(int arg) {
        return arg + nobranch(true) + nobranch(false) + nobranch(true) + nobranch(false);
    }

    static int nobranch(boolean f) {
        if (f) {
            return 0;
        }
        return 1;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1);
    }

}
