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
public class Narrow_char02 extends JTTTest {

    static class Char {

        char foo;
    }

    static Char val = new Char();

    public static char test(char b) {
        val.foo = b;
        return val.foo;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", ((char) 0));
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", ((char) 1));
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", ((char) 255));
    }

    @Test
    public void run3() throws Throwable {
        runTest("test", ((char) 65000));
    }

}
