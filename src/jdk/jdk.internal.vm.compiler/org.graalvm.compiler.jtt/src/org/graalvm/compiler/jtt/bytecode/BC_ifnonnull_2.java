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


package org.graalvm.compiler.jtt.bytecode;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class BC_ifnonnull_2 extends JTTTest {

    public static boolean test(Object a) {
        return a != null;
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
