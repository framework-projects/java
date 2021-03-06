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
public class Fold_Int01 extends JTTTest {

    public static int test(int arg) {
        if (arg == 0) {
            return add();
        }
        if (arg == 1) {
            return sub();
        }
        if (arg == 2) {
            return mul();
        }
        if (arg == 3) {
            return div();
        }
        if (arg == 4) {
            return mod();
        }
        if (arg == 5) {
            return and();
        }
        if (arg == 6) {
            return or();
        }
        if (arg == 7) {
            return xor();
        }
        return 0;
    }

    public static int add() {
        int x = 3;
        return x + 7;
    }

    public static int sub() {
        int x = 15;
        return x - 4;
    }

    public static int mul() {
        int x = 6;
        return x * 2;
    }

    public static int div() {
        int x = 26;
        return x / 2;
    }

    public static int mod() {
        int x = 29;
        return x % 15;
    }

    public static int and() {
        int x = 31;
        return x & 15;
    }

    public static int or() {
        int x = 16;
        return x | 16;
    }

    public static int xor() {
        int x = 0;
        return x ^ 17;
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

    @Test
    public void run3() throws Throwable {
        runTest("test", 3);
    }

    @Test
    public void run4() throws Throwable {
        runTest("test", 4);
    }

    @Test
    public void run5() throws Throwable {
        runTest("test", 5);
    }

    @Test
    public void run6() throws Throwable {
        runTest("test", 6);
    }

    @Test
    public void run7() throws Throwable {
        runTest("test", 7);
    }

}
