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

public class BC_getfield_d extends JTTTest {

    static class FieldHolder {
        FieldHolder(double field) {
            this.field = field;
        }

        private double field;
    }

    public static double test(FieldHolder object) {
        return object.field;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", new FieldHolder(0.0D));
    }

    @Test
    public void run1() throws Throwable {
        runTest("test", new FieldHolder(Double.MAX_VALUE));
    }

    @Test
    public void run2() throws Throwable {
        runTest("test", new FieldHolder(Double.MIN_VALUE));
    }
}
