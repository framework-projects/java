/*
 * Copyright (c) 2011, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.replacements;

import jdk.vm.ci.code.TargetDescription;
import jdk.vm.ci.meta.JavaKind;
import org.graalvm.compiler.api.directives.GraalDirectives;
import org.graalvm.compiler.api.replacements.Snippet;
import org.graalvm.compiler.debug.DebugHandlersFactory;
import org.graalvm.compiler.hotspot.meta.HotSpotProviders;
import org.graalvm.compiler.nodes.java.DynamicNewArrayNode;
import org.graalvm.compiler.nodes.java.NewArrayNode;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.replacements.SnippetTemplate.AbstractTemplates;
import org.graalvm.compiler.replacements.SnippetTemplate.SnippetInfo;
import org.graalvm.compiler.replacements.Snippets;
import org.graalvm.compiler.replacements.arraycopy.ArrayCopyCallNode;

import java.util.EnumMap;

import static org.graalvm.compiler.hotspot.GraalHotSpotVMConfigBase.INJECTED_VMCONFIG;

public class ObjectCloneSnippets implements Snippets {

    public static class Templates extends AbstractTemplates {

        final EnumMap<JavaKind, SnippetInfo> arrayCloneMethods = new EnumMap<>(JavaKind.class);

        public Templates(OptionValues options, Iterable<DebugHandlersFactory> factories, HotSpotProviders providers, TargetDescription target) {
            super(options, factories, providers, providers.getSnippetReflection(), target);
            arrayCloneMethods.put(JavaKind.Boolean, snippet(ObjectCloneSnippets.class, "booleanArrayClone"));
            arrayCloneMethods.put(JavaKind.Byte, snippet(ObjectCloneSnippets.class, "byteArrayClone"));
            arrayCloneMethods.put(JavaKind.Char, snippet(ObjectCloneSnippets.class, "charArrayClone"));
            arrayCloneMethods.put(JavaKind.Short, snippet(ObjectCloneSnippets.class, "shortArrayClone"));
            arrayCloneMethods.put(JavaKind.Int, snippet(ObjectCloneSnippets.class, "intArrayClone"));
            arrayCloneMethods.put(JavaKind.Float, snippet(ObjectCloneSnippets.class, "floatArrayClone"));
            arrayCloneMethods.put(JavaKind.Long, snippet(ObjectCloneSnippets.class, "longArrayClone"));
            arrayCloneMethods.put(JavaKind.Double, snippet(ObjectCloneSnippets.class, "doubleArrayClone"));
            arrayCloneMethods.put(JavaKind.Object, snippet(ObjectCloneSnippets.class, "objectArrayClone"));
        }
    }

    @Snippet
    public static boolean[] booleanArrayClone(boolean[] src) {
        boolean[] result = (boolean[]) NewArrayNode.newUninitializedArray(Boolean.TYPE, src.length);
        ArrayCopyCallNode.disjointArraycopy(src, 0, result, 0, src.length, JavaKind.Boolean, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }

    @Snippet
    public static byte[] byteArrayClone(byte[] src) {
        byte[] result = (byte[]) NewArrayNode.newUninitializedArray(Byte.TYPE, src.length);
        ArrayCopyCallNode.disjointArraycopy(src, 0, result, 0, src.length, JavaKind.Byte, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }

    @Snippet
    public static short[] shortArrayClone(short[] src) {
        short[] result = (short[]) NewArrayNode.newUninitializedArray(Short.TYPE, src.length);
        ArrayCopyCallNode.disjointArraycopy(src, 0, result, 0, src.length, JavaKind.Short, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }

    @Snippet
    public static char[] charArrayClone(char[] src) {
        char[] result = (char[]) NewArrayNode.newUninitializedArray(Character.TYPE, src.length);
        ArrayCopyCallNode.disjointArraycopy(src, 0, result, 0, src.length, JavaKind.Char, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }

    @Snippet
    public static int[] intArrayClone(int[] src) {
        int[] result = (int[]) NewArrayNode.newUninitializedArray(Integer.TYPE, src.length);
        ArrayCopyCallNode.disjointArraycopy(src, 0, result, 0, src.length, JavaKind.Int, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }

    @Snippet
    public static float[] floatArrayClone(float[] src) {
        float[] result = (float[]) NewArrayNode.newUninitializedArray(Float.TYPE, src.length);
        ArrayCopyCallNode.disjointArraycopy(src, 0, result, 0, src.length, JavaKind.Float, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }

    @Snippet
    public static long[] longArrayClone(long[] src) {
        long[] result = (long[]) NewArrayNode.newUninitializedArray(Long.TYPE, src.length);
        ArrayCopyCallNode.disjointArraycopy(src, 0, result, 0, src.length, JavaKind.Long, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }

    @Snippet
    public static double[] doubleArrayClone(double[] src) {
        double[] result = (double[]) NewArrayNode.newUninitializedArray(Double.TYPE, src.length);
        ArrayCopyCallNode.disjointArraycopy(src, 0, result, 0, src.length, JavaKind.Double, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }

    @Snippet
    public static Object[] objectArrayClone(Object[] src) {
        /* Since this snippet is lowered early the array must be initialized */
        Object[] result = (Object[]) DynamicNewArrayNode.newArray(GraalDirectives.guardingNonNull(src.getClass().getComponentType()), src.length, JavaKind.Object);
        ArrayCopyCallNode.disjointUninitializedArraycopy(src, 0, result, 0, src.length, JavaKind.Object, HotSpotReplacementsUtil.getHeapWordSize(INJECTED_VMCONFIG));
        return result;
    }
}
