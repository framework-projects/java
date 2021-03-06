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

public final class Object_notify02 extends JTTTest {

    static final Object object = new Object();

    public static boolean test() {
        synchronized (object) {
            object.notify();
        }
        return true;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test");
    }

}
