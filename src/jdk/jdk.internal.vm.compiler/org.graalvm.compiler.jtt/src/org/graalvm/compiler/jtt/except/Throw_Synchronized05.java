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
/*
 */


package org.graalvm.compiler.jtt.except;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

public class Throw_Synchronized05 extends JTTTest {

    private static class TestClass {
        @SuppressWarnings("unused")
        public synchronized boolean test2(int i) throws Exception {
            try {
                throw new Exception();
            } catch (Exception e) {
                // do nothing and then rethrow
                throw e;
            }
        }
    }

    static final TestClass object = new TestClass();

    public static boolean test(int i) throws Exception {
        if (i == 0) {
            return true;
        }
        return object.test2(i);
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1);
    }

}
