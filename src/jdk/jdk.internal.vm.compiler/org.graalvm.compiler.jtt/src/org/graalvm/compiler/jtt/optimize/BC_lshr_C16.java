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


package org.graalvm.compiler.jtt.optimize;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class BC_lshr_C16 extends JTTTest {

    public static long test(long a) {
        return a >> 16;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 87224824140L);
    }

}
