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
public class LoopUnroll extends JTTTest {

    public static int test(int input) {
        int ret = 2;
        int current = input;
        for (int i = 0; i < 7; i++) {
            ret *= 2 + current;
            current /= 50;
        }
        return ret;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 42);
    }

}
