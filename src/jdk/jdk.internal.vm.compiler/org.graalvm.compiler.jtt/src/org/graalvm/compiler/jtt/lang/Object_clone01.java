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
 */


package org.graalvm.compiler.jtt.lang;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

public class Object_clone01 extends JTTTest {

    private static class TestClass {
        @SuppressWarnings("unused")
        private boolean tryClone(int i) throws CloneNotSupportedException {
            return this == this.clone();
        }
    }

    static final TestClass field = new TestClass();

    public static boolean test(int i) throws CloneNotSupportedException {
        return field.tryClone(i);
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

}
