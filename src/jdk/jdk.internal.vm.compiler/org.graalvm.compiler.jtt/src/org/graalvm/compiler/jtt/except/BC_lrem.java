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

public class BC_lrem extends JTTTest {

    public static long test(long a, long b) {
        return a % b;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 1L, 2L);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 11L, 0L);
    }

}
