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
/*
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
 */


package org.graalvm.compiler.jtt.except;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class Catch_NASE_2 extends JTTTest {

    @SuppressWarnings("unused")
    public static int test(int a) {
        try {
            DummyTestClass[] v = new DummyTestClass[a];
            if (v != null) {
                return v.length;
            }
            return -1;
        } catch (NegativeArraySizeException e) {
            return 100;
        }
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", -1);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", -34);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 1);
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", 20);
    }

}
