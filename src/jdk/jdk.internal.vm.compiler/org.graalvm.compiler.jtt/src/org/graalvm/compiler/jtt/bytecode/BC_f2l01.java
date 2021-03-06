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


package org.graalvm.compiler.jtt.bytecode;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class BC_f2l01 extends JTTTest {

    public static long test(float d) {
        return (long) d;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0.0f);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1.0f);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", -1.06f);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", -156.82743f);
    }

}
