/*
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.jtt.optimize;

import org.graalvm.compiler.jtt.JTTTest;
import org.junit.Test;

/*
 */
public class SchedulingBug_01 extends JTTTest {

    private static class VolatileBoxHolder {
        volatile Integer box;
    }

    public static int test(VolatileBoxHolder a, VolatileBoxHolder b) {
        int value = a.box;
        int result = 0;
        if (b.box != null) {
            result += value;
        }
        return result + value;
    }

    @Test
    public void run0() throws Throwable {
        runTest("test", new VolatileBoxHolder(), new VolatileBoxHolder());
    }

}
