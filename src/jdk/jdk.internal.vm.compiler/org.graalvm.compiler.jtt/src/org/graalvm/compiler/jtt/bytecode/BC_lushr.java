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
public class BC_lushr extends JTTTest {

    public static long test(long a, int b) {
        return a >>> b;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 1L, 2);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 67L, 2);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 31L, 1);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", 6L, 4);
    }

    @Test
    public void run4() throws Throwable {
        runTest("test", -2147483648L, 16);
    }

}
