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
public class BC_lookupswitch05 extends JTTTest {

    public static Object test(int a) {
        switch (a) {
            default:
                return new String();
        }
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

}
