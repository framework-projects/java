/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
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

import static org.graalvm.compiler.hotspot.GraalHotSpotVMConfigBase.INJECTED_METAACCESS;
import static org.graalvm.compiler.replacements.ReplacementsUtil.getArrayBaseOffset;
import static org.graalvm.compiler.replacements.SnippetTemplate.DEFAULT_REPLACER;

import org.graalvm.compiler.api.replacements.Snippet;
import org.graalvm.compiler.api.replacements.Snippet.ConstantParameter;
import org.graalvm.compiler.debug.DebugHandlersFactory;
import org.graalvm.compiler.hotspot.meta.HotSpotProviders;
import org.graalvm.compiler.nodes.NamedLocationIdentity;
import org.graalvm.compiler.nodes.debug.StringToBytesNode;
import org.graalvm.compiler.nodes.extended.RawStoreNode;
import org.graalvm.compiler.nodes.java.NewArrayNode;
import org.graalvm.compiler.nodes.spi.LoweringTool;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.replacements.SnippetTemplate;
import org.graalvm.compiler.replacements.SnippetTemplate.AbstractTemplates;
import org.graalvm.compiler.replacements.SnippetTemplate.Arguments;
import org.graalvm.compiler.replacements.SnippetTemplate.SnippetInfo;
import org.graalvm.compiler.replacements.Snippets;
import org.graalvm.compiler.replacements.nodes.CStringConstant;
import org.graalvm.compiler.word.Word;
import jdk.internal.vm.compiler.word.LocationIdentity;

import jdk.vm.ci.code.TargetDescription;
import jdk.vm.ci.meta.JavaKind;

/**
 * The {@code StringToBytesSnippets} contains a snippet for lowering {@link StringToBytesNode}.
 */
public class StringToBytesSnippets implements Snippets {

    public static final LocationIdentity CSTRING_LOCATION = NamedLocationIdentity.immutable("CString location");

    @Snippet
    public static byte[] transform(@ConstantParameter String compilationTimeString) {
        int i = compilationTimeString.length();
        byte[] array = (byte[]) NewArrayNode.newUninitializedArray(byte.class, i);
        Word cArray = CStringConstant.cstring(compilationTimeString);
        while (i-- > 0) {
            // array[i] = cArray.readByte(i);
            RawStoreNode.storeByte(array, getArrayBaseOffset(INJECTED_METAACCESS, JavaKind.Byte) + i, cArray.readByte(i, CSTRING_LOCATION), JavaKind.Byte,
                            NamedLocationIdentity.getArrayLocation(JavaKind.Byte));
        }
        return array;
    }

    public static class Templates extends AbstractTemplates {

        private final SnippetInfo create;

        public Templates(OptionValues options, Iterable<DebugHandlersFactory> factories, HotSpotProviders providers, TargetDescription target) {
            super(options, factories, providers, providers.getSnippetReflection(), target);
            create = snippet(StringToBytesSnippets.class, "transform", NamedLocationIdentity.getArrayLocation(JavaKind.Byte));
        }

        public void lower(StringToBytesNode stringToBytesNode, LoweringTool tool) {
            Arguments args = new Arguments(create, stringToBytesNode.graph().getGuardsStage(), tool.getLoweringStage());
            args.addConst("compilationTimeString", stringToBytesNode.getValue());
            SnippetTemplate template = template(stringToBytesNode, args);
            template.instantiate(providers.getMetaAccess(), stringToBytesNode, DEFAULT_REPLACER, args);
        }

    }

}
