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


package org.graalvm.compiler.jtt.lang;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public final class Long_greater01 extends JTTTest {

    public static boolean test(long i) {
        if (i > 0L) {
            return true;
        }
        return false;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", -9223372036854775808L);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", -2L);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", -1L);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", 0L);
    }

    @Test
    public void run4() throws Throwable {
        runTest("test", 1L);
    }

    @Test
    public void run5() throws Throwable {
        runTest("test", 2L);
    }

    @Test
    public void run6() throws Throwable {
        runTest("test", 9223372036854775807L);
    }

}
