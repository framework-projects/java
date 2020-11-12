/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.loop.test;

import static org.graalvm.compiler.core.common.util.ReversedList.reversed;

import java.util.HashSet;
import java.util.Set;

import org.graalvm.compiler.core.test.GraalCompilerTest;
import org.graalvm.compiler.loop.LoopEx;
import org.graalvm.compiler.loop.LoopsData;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.junit.Assert;
import org.junit.Test;

public class LoopsDataTest extends GraalCompilerTest {

    @SuppressWarnings("unused")
    private static int loopy(int n) {
        int t = n;
        for (int i = 0; i < n; i++) {
            t += i * n;
        }
        while (t != 0) {
            if (t > 0) {
                for (int i = 0; i < 2 * n; i++) {
                    t -= n + i;
                }
            } else {
                for (int i = 0; i < n / 2; i++) {
                    t += i * i;
                    for (int j = 0; j < n; j++) {
                        t += i * j * (((i + j) % 2) * 2 - 1);
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    t += i * j * (((i + j) % 2) * 2 - 1);
                    for (int k = 0; k < n; k++) {
                        t += i * k * (((i + k) % 2) * 2 - 1);
                    }
                }
            }
            if (t % 17 == 0) {
                return t;
            }
        }
        return -1;
    }

    @Test
    public void sanityTests() {
        LoopsData loops = getLoopsData();
        Assert.assertEquals(8, loops.outerFirst().size());
        Assert.assertEquals(1, loops.outerFirst().get(0).loop().getDepth());
        Assert.assertEquals(1, loops.outerFirst().get(1).loop().getDepth());
        Assert.assertEquals(2, loops.outerFirst().get(2).loop().getDepth());
        Assert.assertEquals(3, loops.outerFirst().get(3).loop().getDepth());
        Assert.assertEquals(2, loops.outerFirst().get(4).loop().getDepth());
        Assert.assertEquals(2, loops.outerFirst().get(5).loop().getDepth());
        Assert.assertEquals(3, loops.outerFirst().get(6).loop().getDepth());
        Assert.assertEquals(4, loops.outerFirst().get(7).loop().getDepth());

        for (LoopEx loop : loops.loops()) {
            if (loop.parent() != null) {
                Assert.assertEquals(loop.parent().loop().getDepth() + 1, loop.loop().getDepth());
            }
        }
    }

    @Test
    public void testInnerFirst() {
        LoopsData loops = getLoopsData();

        Set<LoopEx> seen = new HashSet<>();
        for (LoopEx loop : reversed(loops.outerFirst())) {
            assertFalse(seen.contains(loop), "%s has already been seen", loop);
            if (loop.parent() != null) {
                assertFalse(seen.contains(loop.parent()), "%s's parent (%s) should not have already been seen", loop, loop.parent());
            }
            seen.add(loop);
        }
    }

    @Test
    public void testouterFirst() {
        LoopsData loops = getLoopsData();

        Set<LoopEx> seen = new HashSet<>();
        for (LoopEx loop : loops.outerFirst()) {
            assertFalse(seen.contains(loop), "%s has already been seen", loop);
            if (loop.parent() != null) {
                assertTrue(seen.contains(loop.parent()), "%s's parent (%s) should have already been seen", loop, loop.parent());
            }
            seen.add(loop);
        }
    }

    private LoopsData getLoopsData() {
        StructuredGraph graph = parseEager("loopy", StructuredGraph.AllowAssumptions.NO);
        return new LoopsData(graph);
    }
}
