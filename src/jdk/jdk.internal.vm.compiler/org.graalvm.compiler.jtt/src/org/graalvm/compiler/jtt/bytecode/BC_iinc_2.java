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
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
 */


package org.graalvm.compiler.jtt.bytecode;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class BC_iinc_2 extends JTTTest {

    public static int test(int a) {
        int arg = a;
        arg += 2;
        return arg;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 1);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 2);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 4);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", -2);
    }

}
