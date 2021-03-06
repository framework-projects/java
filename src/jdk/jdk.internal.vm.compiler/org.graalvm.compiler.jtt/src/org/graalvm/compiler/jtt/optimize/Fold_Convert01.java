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
public class Fold_Convert01 extends JTTTest {

    public static int test(int arg) {
        if (arg == 0) {
            return i2b();
        }
        if (arg == 1) {
            return i2s();
        }
        if (arg == 2) {
            return i2c();
        }
        return 0;
    }

    public static int i2b() {
        int x = 0x00000080;
        return (byte) x;
    }

    public static int i2s() {
        int x = 0x00008000;
        return (short) x;
    }

    public static int i2c() {
        int x = 0xffffffff;
        return (char) x;
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
