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


package org.graalvm.compiler.jtt.lang;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ProcessEnvironment_init extends JTTTest {

    private static HashMap<Object, Object> theEnvironment;
    public static Map<Object, Object> theUnmodifiableEnvironment;

    public static int test(int v) {

        byte[][] environ = environ();
        theEnvironment = new HashMap<>(environ.length / 2 + 3);

        for (int i = environ.length - 1; i > 0; i -= 2) {
            theEnvironment.put(Variable.valueOf(environ[i - 1]), Value.valueOf(environ[i]));
        }

        theUnmodifiableEnvironment = Collections.unmodifiableMap(new StringEnvironment(theEnvironment));

        return v;
    }

    @SuppressWarnings("serial")
    private static final class StringEnvironment extends HashMap<Object, Object> {

        @SuppressWarnings("unused")
        StringEnvironment(HashMap<Object, Object> theenvironment) {
        }
    }

    private static final class Variable {

        @SuppressWarnings("unused")
        public static Object valueOf(byte[] bs) {
            return new Object();
        }
    }

    private static final class Value {

        @SuppressWarnings("unused")
        public static Object valueOf(byte[] bs) {
            return new Object();
        }
    }

    private static byte[][] environ() {
        return new byte[3][3];
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", 7);
    }

}