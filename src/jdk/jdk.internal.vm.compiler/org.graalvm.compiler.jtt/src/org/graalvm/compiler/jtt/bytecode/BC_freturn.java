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
public class BC_freturn extends JTTTest {

    public static float test(float a) {
        return a;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0.0f);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1.1f);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", -1.4f);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", 256.33f);
    }

    @Test
    public void run4() throws Throwable {
        runTest("test", 1000.001f);
    }

}
