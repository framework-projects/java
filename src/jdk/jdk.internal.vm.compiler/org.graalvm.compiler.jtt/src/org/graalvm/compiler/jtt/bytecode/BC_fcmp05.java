/*
 * Copyright (c) 2008, 2018, Oracle and/or its affiliates. All rights reserved.
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
public class BC_fcmp05 extends JTTTest {

    public static boolean test(float a) {
        return (a / a) >= 0.0f;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", -1.0f);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1.0f);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 0.0f);
    }

}
