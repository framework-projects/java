/*
 * Copyright (c) 2015, 2019, Oracle and/or its affiliates. All rights reserved.
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
import jdk.vm.ci.meta.JavaConstant;
import jdk.vm.ci.meta.PrimitiveConstant;
import org.graalvm.compiler.core.common.LIRKind;
import org.graalvm.compiler.lir.VirtualStackSlot;
import org.graalvm.compiler.lir.framemap.FrameMapBuilder;
import org.graalvm.compiler.lir.gen.LIRGeneratorTool;
import org.graalvm.compiler.lir.jtt.LIRTest;
import org.graalvm.compiler.lir.jtt.LIRTestSpecification;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assume.assumeTrue;

public class ConstantStackMoveTest extends LIRTest {
    @Before
    public void checkAMD64() {
        assumeTrue("skipping AMD64 specific test", getTarget().arch instanceof AMD64);
    }

    private static class LoadConstantStackSpec extends LIRTestSpecification {
        protected final Object primitive;

        LoadConstantStackSpec(Object primitive) {
            this.primitive = primitive;
        }

        @Override
        public void generate(LIRGeneratorTool gen) {
            FrameMapBuilder frameMapBuilder = gen.getResult().getFrameMapBuilder();
            // create slots
            PrimitiveConstant constantValue = JavaConstant.forBoxedPrimitive(primitive);
            VirtualStackSlot s1 = frameMapBuilder.allocateSpillSlot(LIRKind.fromJavaKind(gen.target().arch, constantValue.getJavaKind()));
            // move stuff around
            gen.emitMoveConstant(s1, constantValue);
            gen.emitBlackhole(s1);
            setResult(gen.emitMove(s1));
        }
    }

    private static final class LoadConstantStackSpecByte extends LoadConstantStackSpec {
        LoadConstantStackSpecByte(byte primitive) {
            super(primitive);
        }

        byte get() {
            return (Byte) primitive;
        }
    }

    private static final class LoadConstantStackSpecShort extends LoadConstantStackSpec {
        LoadConstantStackSpecShort(short primitive) {
            super(primitive);
        }

        short get() {
            return (Short) primitive;
        }
    }

    private static final class LoadConstantStackSpecInteger extends LoadConstantStackSpec {
        LoadConstantStackSpecInteger(int primitive) {
            super(primitive);
        }

        int get() {
            return (Integer) primitive;
        }
    }

    private static final class LoadConstantStackSpecLong extends LoadConstantStackSpec {
        LoadConstantStackSpecLong(long primitive) {
            super(primitive);
        }

        long get() {
            return (Long) primitive;
        }
    }

    private static final class LoadConstantStackSpecFloat extends LoadConstantStackSpec {
        LoadConstantStackSpecFloat(float primitive) {
            super(primitive);
        }

        float get() {
            return (Float) primitive;
        }
    }

    private static final class LoadConstantStackSpecDouble extends LoadConstantStackSpec {
        LoadConstantStackSpecDouble(double primitive) {
            super(primitive);
        }

        double get() {
            return (Double) primitive;
        }
    }

    private static final LoadConstantStackSpecByte stackCopyByte = new LoadConstantStackSpecByte(Byte.MAX_VALUE);
    private static final LoadConstantStackSpecShort stackCopyShort = new LoadConstantStackSpecShort(Short.MAX_VALUE);
    private static final LoadConstantStackSpecInteger stackCopyInt = new LoadConstantStackSpecInteger(Integer.MAX_VALUE);
    private static final LoadConstantStackSpecLong stackCopyLong = new LoadConstantStackSpecLong(Long.MAX_VALUE);
    private static final LoadConstantStackSpecFloat stackCopyFloat = new LoadConstantStackSpecFloat(Float.MAX_VALUE);
    private static final LoadConstantStackSpecDouble stackCopyDouble = new LoadConstantStackSpecDouble(Double.MAX_VALUE);

    @LIRIntrinsic
    public static byte testCopyByte(LoadConstantStackSpecByte spec) {
        return spec.get();
    }

    public byte testByte() {
        return testCopyByte(stackCopyByte);
    }

    @Test
    public void runByte() {
        runTest("testByte");
    }

    @LIRIntrinsic
    public static short testCopyShort(LoadConstantStackSpecShort spec) {
        return spec.get();
    }

    public short testShort() {
        return testCopyShort(stackCopyShort);
    }

    @Test
    public void runShort() {
        runTest("testShort");
    }

    @LIRIntrinsic
    public static int testCopyInt(LoadConstantStackSpecInteger spec) {
        return spec.get();
    }

    public int testInt() {
        return testCopyInt(stackCopyInt);
    }

    @Test
    public void runInt() {
        runTest("testInt");
    }

    @LIRIntrinsic
    public static long testCopyLong(LoadConstantStackSpecLong spec) {
        return spec.get();
    }

    public long testLong() {
        return testCopyLong(stackCopyLong);
    }

    @Test
    public void runLong() {
        runTest("testLong");
    }

    @LIRIntrinsic
    public static float testCopyFloat(LoadConstantStackSpecFloat spec) {
        return spec.get();
    }

    public float testFloat() {
        return testCopyFloat(stackCopyFloat);
    }

    @Test
    public void runFloat() {
        runTest("testFloat");
    }

    @LIRIntrinsic
    public static double testCopyDouble(LoadConstantStackSpecDouble spec) {
        return spec.get();
    }

    public double testDouble() {
        return testCopyDouble(stackCopyDouble);
    }

    @Test
    public void runDouble() {
        runTest("testDouble");
    }

}
