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


package org.graalvm.compiler.jtt.reflect;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

/*
 */
public class Invoke_main02 extends JTTTest {

    public static class TestClass {
        public static void main(String[] args) {
            field = args[0];
        }
    }

    static String field;

    public static String test(String input) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        field = null;
        final String[] args = {input};
        TestClass.class.getDeclaredMethod("main", String[].class).invoke(null, new Object[]{args});
        return field;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", "test1");
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", "test2");
    }

}
