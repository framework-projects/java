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


package org.graalvm.compiler.jtt.lang;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class Long_reverseBytes01 extends JTTTest {

    public static long test(long val) {
        return Long.reverseBytes(val);
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0x1122334455667708L);
    }

}
