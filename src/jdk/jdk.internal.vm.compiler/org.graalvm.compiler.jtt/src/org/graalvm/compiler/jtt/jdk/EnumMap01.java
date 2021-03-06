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

import java.util.EnumMap;

/*
 */
public class EnumMap01 extends JTTTest {

    private static final EnumMap<Enum, String> map = new EnumMap<>(Enum.class);

    static {
        map.put(Enum.A, "A");
        map.put(Enum.B, "B");
        map.put(Enum.C, "C");
    }

    public static String test(int i) {
        return map.get(Enum.values()[i]);
    }

    private enum Enum {
        A,
        B,
        C
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 0);
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", 1);
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", 2);
    }

}
