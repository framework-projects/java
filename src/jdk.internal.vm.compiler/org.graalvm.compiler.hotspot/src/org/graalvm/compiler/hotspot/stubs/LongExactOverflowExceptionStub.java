/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.stubs;

import org.graalvm.compiler.api.replacements.Snippet;
import org.graalvm.compiler.api.replacements.Snippet.ConstantParameter;
import org.graalvm.compiler.debug.GraalError;
import org.graalvm.compiler.hotspot.HotSpotForeignCallLinkage;
import org.graalvm.compiler.hotspot.meta.HotSpotProviders;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.replacements.nodes.CStringConstant;
import org.graalvm.compiler.word.Word;

import jdk.vm.ci.code.Register;

public class LongExactOverflowExceptionStub extends CreateExceptionStub {
    public LongExactOverflowExceptionStub(OptionValues options, HotSpotProviders providers, HotSpotForeignCallLinkage linkage) {
        super("createLongExactOverflowException", options, providers, linkage);
    }

    @Override
    protected Object getConstantParameterValue(int index, String name) {
        GraalError.guarantee(index == 0, "unknown parameter %s at index %d", name, index);
        return providers.getRegisters().getThreadRegister();
    }

    @Snippet
    private static Object createLongExactOverflowException(@ConstantParameter Register threadRegister) {
        Word msg = CStringConstant.cstring("long overflow");
        return createException(threadRegister, ArithmeticException.class, msg);
    }
}
