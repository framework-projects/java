/*
 * Copyright (c) 2013, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.core.amd64.test;

import jdk.vm.ci.amd64.AMD64;
import org.graalvm.compiler.core.test.backend.AllocatorTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.graalvm.compiler.core.common.GraalOptions.RegisterPressure;
import static org.junit.Assume.assumeTrue;

public class AMD64AllocatorTest extends AllocatorTest {

    @Before
    public void checkAMD64() {
        assumeTrue("skipping AMD64 specific test", getTarget().arch instanceof AMD64);
        assumeTrue("RegisterPressure is set -> skip", RegisterPressure.getValue(getInitialOptions()) == null);
    }

    @Test
    public void test1() {
        testAllocation("test1snippet", 3, 0, 0);
    }

    public static long test1snippet(long x) {
        return x + 5;
    }

    @Test
    public void test2() {
        testAllocation("test2snippet", 3, 0, 0);
    }

    public static long test2snippet(long x) {
        return x * 5;
    }

    @Ignore
    @Test
    public void test3() {
        testAllocation("test3snippet", 4, 1, 0);
    }

    public static long test3snippet(long x) {
        return x / 3 + x % 3;
    }

}
