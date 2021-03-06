/*
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.lir.test;

import jdk.vm.ci.code.BailoutException;
import jdk.vm.ci.meta.AllocatableValue;
import jdk.vm.ci.meta.JavaConstant;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.core.common.LIRKind;
import org.graalvm.compiler.hotspot.HotSpotBackend;
import org.graalvm.compiler.lir.framemap.FrameMapBuilder;
import org.graalvm.compiler.lir.gen.LIRGeneratorTool;
import org.graalvm.compiler.lir.jtt.LIRTest;
import org.graalvm.compiler.lir.jtt.LIRTestSpecification;
import org.graalvm.compiler.nodes.SafepointNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderConfiguration;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin;
import org.junit.Test;

public class ExceedMaxOopMapStackOffset extends LIRTest {

    /**
     * Allocate lots of stacks slots and initialize them with a constant.
     */
    private static class WriteStackSlotsSpec extends LIRTestSpecification {
        private final JavaConstant constant;

        WriteStackSlotsSpec(JavaConstant constant) {
            this.constant = constant;
        }

        @Override
        public void generate(LIRGeneratorTool gen) {
            FrameMapBuilder frameMapBuilder = gen.getResult().getFrameMapBuilder();
            LIRKind lirKind = LIRKind.reference(gen.target().arch.getPlatformKind(constant.getJavaKind()));
            // create slots
            for (int i = 0; i < slots.length; i++) {
                AllocatableValue src = gen.emitLoadConstant(lirKind, constant);
                slots[i] = frameMapBuilder.allocateSpillSlot(lirKind);
                gen.emitMove(slots[i], src);
            }
        }
    }

    /**
     * Read stacks slots and move their content into a blackhole.
     */
    private static class ReadStackSlotsSpec extends LIRTestSpecification {

        ReadStackSlotsSpec() {
        }

        @Override
        public void generate(LIRGeneratorTool gen) {
            for (int i = 0; i < slots.length; i++) {
                gen.emitBlackhole(gen.emitMove(slots[i]));
            }
        }
    }

    @Override
    protected GraphBuilderConfiguration editGraphBuilderConfiguration(GraphBuilderConfiguration conf) {
        InvocationPlugin safepointPlugin = new InvocationPlugin() {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                b.add(new SafepointNode());
                return true;
            }
        };
        conf.getPlugins().getInvocationPlugins().register(safepointPlugin, getClass(), "safepoint");
        return super.editGraphBuilderConfiguration(conf);
    }

    /*
     * Safepoint Snippet
     */
    private static void safepoint() {
    }

    private static AllocatableValue[] slots;

    private static final LIRTestSpecification readStackObjects = new ReadStackSlotsSpec();

    @SuppressWarnings("unused")
    @LIRIntrinsic
    public static void instrinsic(LIRTestSpecification spec) {
    }

    private static final LIRTestSpecification writeStackObjects = new WriteStackSlotsSpec(JavaConstant.NULL_POINTER);

    public void testStackObjects() {
        instrinsic(writeStackObjects);
        safepoint();
        instrinsic(readStackObjects);
    }

    @Test
    public void runStackObjects() {
        int max = ((HotSpotBackend) getBackend()).getRuntime().getVMConfig().maxOopMapStackOffset;
        if (max == Integer.MAX_VALUE) {
            max = 16 * 1024 - 64;
        }
        try {
            int numSlots = (max / 8) + 1;
            slots = new AllocatableValue[numSlots];
            runTest("testStackObjects");
        } catch (BailoutException e) {
            return;
        }
        fail("Expected exception BailoutException wasn't thrown");
    }
}
