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


package org.graalvm.compiler.jtt.threads;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

public final class Thread_currentThread01 extends JTTTest {

    public static boolean test() {
        return Thread.currentThread() != null;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test");
    }

}
