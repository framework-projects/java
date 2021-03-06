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
 * Tests constant folding of integer operations.
 */
public class VN_Cast02 extends JTTTest {

    private static class TestClass {
        int field = 9;
    }

    private static boolean cond = true;
    static final Object object = new TestClass();

    public static int test(int arg) {
        if (arg == 0) {
            return test1();
        }
        if (arg == 1) {
            return test2();
        }
        if (arg == 2) {
            return test3();
        }
        return 0;
    }

    private static int test1() {
        Object o = object;
        TestClass a = (TestClass) o;
        if (cond) {
            TestClass b = (TestClass) o;
            return a.field + b.field;
        }
        return 0;
    }

    private static int test2() {
        Object obj = new TestClass();
        TestClass a = (TestClass) obj;
        if (cond) {
            TestClass b = (TestClass) obj;
            return a.field + b.field;
        }
        return 0;
    }

    @SuppressWarnings("all")
    private static int test3() {
        Object o = null;
        TestClass a = (TestClass) o;
        if (cond) {
            TestClass b = (TestClass) o;
            return a.field + b.field;
        }
        return 0;
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
