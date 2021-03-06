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


package org.graalvm.compiler.jtt.optimize;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class NCE_FlowSensitive03 extends JTTTest {

    public static String test(String arg) {
        if ("x".equals(arg)) {
            if (arg == null) {
                return "null";
            }
        } else {
            if (arg == null) {
                return "null";
            }
        }
        // arg cannot be null here
        return arg.toString();
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", (Object) null);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", "x");
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", "yay");
    }

}
