/*
 * Copyright (c) 2011, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.jtt.hotspot;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/**
 * @test
 * @bug 6753639
 * @summary Strange optimisation in for loop with cyclic integer condition
 *
 * @run main/othervm -Xbatch Test6753639
 */
// @formatter:off
public class Test6753639 extends JTTTest {

    public static int test() {
        int end = Integer.MAX_VALUE;
        int count = 0;
        for (int i = Integer.MAX_VALUE - 5; i <= end; i++) {
            count++;
            if (count > 100000) {
                return 95;
            }
        }
        return 97;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test");
    }

}
