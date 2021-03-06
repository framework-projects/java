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


package org.graalvm.compiler.jtt.bytecode;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

public class BC_getfield_c extends JTTTest {

    static class FieldHolder {
        FieldHolder(char field) {
            this.field = field;
        }

        private char field;
    }

    public static char test(FieldHolder object) {
        return object.field;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", new FieldHolder('A'));
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", new FieldHolder(Character.MAX_VALUE));
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", new FieldHolder(Character.MIN_VALUE));
    }
}
