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


package org.graalvm.compiler.jtt.bytecode;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class BC_ifnull extends JTTTest {

    public static int test(Object a) {
        int n = 0;
        if (a != null) {
            n += 1;
        } else {
            n -= 1;
        }
        if (a == null) {
            n -= 1;
        } else {
            n += 1;
        }
        return n;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", (Object) null);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", "");
    }

}
