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


package org.graalvm.compiler.hotspot.meta;

import org.graalvm.compiler.api.replacements.ClassSubstitution;
import org.graalvm.compiler.api.replacements.MethodSubstitution;
import org.graalvm.compiler.hotspot.HotSpotBackend;
import org.graalvm.compiler.nodes.ComputeObjectAddressNode;
import org.graalvm.compiler.serviceprovider.JavaVersionUtil;
import org.graalvm.compiler.word.Word;
import jdk.internal.vm.compiler.word.WordFactory;

@ClassSubstitution(className = {"jdk.internal.misc.Unsafe", "sun.misc.Unsafe"})
public class HotSpotUnsafeSubstitutions {

    public static final String copyMemoryName = JavaVersionUtil.JAVA_SPEC <= 8 ? "copyMemory" : "copyMemory0";

    @SuppressWarnings("unused")
    @MethodSubstitution(isStatic = false)
    static void copyMemory(Object receiver, Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
        Word srcAddr = WordFactory.unsigned(ComputeObjectAddressNode.get(srcBase, srcOffset));
        Word dstAddr = WordFactory.unsigned(ComputeObjectAddressNode.get(destBase, destOffset));
        Word size = WordFactory.signed(bytes);
        HotSpotBackend.unsafeArraycopy(srcAddr, dstAddr, size);
    }
}
