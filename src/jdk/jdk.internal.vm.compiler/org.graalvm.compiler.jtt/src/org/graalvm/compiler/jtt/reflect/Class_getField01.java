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

/*
 */
public class Class_getField01 extends JTTTest {

    public static String field;
    public String field2;
    String field3;

    public static String test(String input) throws NoSuchFieldException {
        return Class_getField01.class.getField(input).getName();
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", "test");
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", "field");
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", "field2");
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", "field3");
    }

}
