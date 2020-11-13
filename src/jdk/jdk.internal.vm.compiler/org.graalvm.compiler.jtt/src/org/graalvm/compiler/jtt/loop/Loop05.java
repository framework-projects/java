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


package org.graalvm.compiler.jtt.loop;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class Loop05 extends JTTTest {

    @SuppressWarnings("unused")
    public static String test(int a) {
        int arg = a;
        int count = 0;
        while (--arg > 0) {
            count++;
            new Object();
        }
        return "ok" + count;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 10);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 25);
    }

}
