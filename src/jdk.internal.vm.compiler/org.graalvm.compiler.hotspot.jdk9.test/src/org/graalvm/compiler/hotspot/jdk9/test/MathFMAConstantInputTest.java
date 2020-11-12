/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.jdk9.test;

import static org.junit.Assume.assumeTrue;

import org.graalvm.compiler.core.test.GraalCompilerTest;
import org.graalvm.compiler.test.AddExports;
import org.junit.Before;
import org.junit.Test;

import jdk.vm.ci.amd64.AMD64;

@AddExports({"java.base/java.lang"})
public final class MathFMAConstantInputTest extends GraalCompilerTest {

    @Before
    public void checkAMD64() {
        assumeTrue("skipping AMD64 specific test", getTarget().arch instanceof AMD64);
    }

    public static float floatFMA() {
        return Math.fma(2.0f, 2.0f, 2.0f);
    }

    @Test
    public void testFloatFMA() {
        test("floatFMA");
    }

    public static double doubleFMA() {
        return Math.fma(2.0d, 2.0d, 2.0d);
    }

    @Test
    public void testDoubleFMA() {
        test("doubleFMA");
    }

}
