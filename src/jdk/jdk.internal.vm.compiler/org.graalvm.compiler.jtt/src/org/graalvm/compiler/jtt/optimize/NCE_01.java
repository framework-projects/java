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
 * Test case for null check elimination.
 */
public class NCE_01 extends JTTTest {

    private static class TestClass {
        int field1 = 22;
        int field2 = 23;
    }

    public static TestClass object = new TestClass();

    public static int test() {
        TestClass o = object;
        int i = o.field1;
        // expected null check elimination here
        return o.field2 + i;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test");
    }

}
