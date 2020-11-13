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


package org.graalvm.compiler.jtt.jdk;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class Class_getName extends JTTTest {

    public static String test(int a) {
        if (a == 0) {
            return String.class.getName();
        }
        return "";
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

}
