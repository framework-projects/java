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


package org.graalvm.compiler.jtt.lang;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

import java.net.URLClassLoader;

/*
 */
public class JDK_ClassLoaders02 extends JTTTest {

    public static boolean test() {
        ClassLoader classLoader = JDK_ClassLoaders02.class.getClassLoader();
        return classLoader == null || classLoader instanceof URLClassLoader;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test");
    }

}
