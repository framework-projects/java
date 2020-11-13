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

import org.graalvm.compiler.api.directives.GraalDirectives;
import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/**
 * Checks that the time between 2 successive calls to {@link System#nanoTime()} is less than 30
 * microseconds at least once in 5_000_000 attempts.
 */
public class System_nanoTime02 extends JTTTest {

    public static boolean test() {
        for (int i = 0; i < 5_000_000; i++) {
            long delta = System.nanoTime() - System.nanoTime();
            if (delta < 30_000) {
                return true;
            }
        }
        if (!GraalDirectives.inCompiledCode()) {
            // We don't care about the result for the interpreter, C1 or C2
            return true;
        }
        return false;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test");
    }
}
