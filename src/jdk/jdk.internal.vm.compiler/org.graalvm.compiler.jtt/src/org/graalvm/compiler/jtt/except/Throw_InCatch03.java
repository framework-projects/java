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
 */


package org.graalvm.compiler.jtt.except;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

public class Throw_InCatch03 extends JTTTest {

    public static boolean test(int i) throws Exception {
        if (i == 0) {
            return true;
        }
        try {
            throwE();
        } catch (Exception e) {
            throw e;
        }
        return false;
    }

    private static void throwE() throws Exception {
        throw new Exception();
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1);
    }

}
