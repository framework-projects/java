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
 * Copyright (c) 2007, 2018, Oracle and/or its affiliates. All rights reserved.
 */


package org.graalvm.compiler.jtt.except;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class Catch_NPE_06 extends JTTTest {

    public static int test(String string) {
        try {
            return string.hashCode();
        } catch (NullPointerException npe) {
            return -1;
        }
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", "");
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", (Object) null);
    }

}
