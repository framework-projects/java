/*
 * Copyright (c) 2010, 2018, Oracle and/or its affiliates. All rights reserved.
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

/*
 */
public class Float_03 extends JTTTest {

    public static boolean test() {
        return Float.floatToIntBits(Float.intBitsToFloat(0x7fc00088)) == 0x7fc00000;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test");
    }

}
