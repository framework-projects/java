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

import jdk.internal.vm.compiler.word.WordFactory;
import jdk.vm.ci.code.BytecodeFrame;
import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.TargetDescription;
import jdk.vm.ci.meta.ResolvedJavaType;
import org.graalvm.compiler.api.replacements.Snippet;
import org.graalvm.compiler.api.replacements.Snippet.ConstantParameter;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.debug.DebugHandlersFactory;
import org.graalvm.compiler.hotspot.meta.HotSpotProviders;
import org.graalvm.compiler.hotspot.meta.HotSpotRegistersProvider;
import org.graalvm.compiler.hotspot.word.HotSpotWordTypes;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.extended.ForeignCallNode;
import org.graalvm.compiler.nodes.java.LoadExceptionObjectNode;
import org.graalvm.compiler.nodes.spi.LoweringTool;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.replacements.SnippetTemplate.AbstractTemplates;
import org.graalvm.compiler.replacements.SnippetTemplate.Arguments;
import org.graalvm.compiler.replacements.SnippetTemplate.SnippetInfo;
import org.graalvm.compiler.replacements.Snippets;
import org.graalvm.compiler.replacements.nodes.ReadRegisterNode;
import org.graalvm.compiler.word.Word;

import static org.graalvm.compiler.hotspot.meta.HotSpotForeignCallsProviderImpl.LOAD_AND_CLEAR_EXCEPTION;
import static org.graalvm.compiler.hotspot.replacements.HotSpotReplacementsUtil.*;
import static org.graalvm.compiler.hotspot.replacements.HotspotSnippetsOptions.LoadExceptionObjectInVM;
import static org.graalvm.compiler.nodes.PiNode.piCastToSnippetReplaceeStamp;
import static org.graalvm.compiler.replacements.SnippetTemplate.DEFAULT_REPLACER;

/**
 * Snippet for loading the exception object at the start of an exception dispatcher.
 * <p>
 * The frame state upon entry to an exception handler is such that it is a
 * {@link BytecodeFrame#rethrowException rethrow exception} state and the stack contains exactly the
 * exception object (per the JVM spec) to rethrow. This means that the code generated for this node
 * must not cause a deoptimization as the runtime/interpreter would not have a valid location to
 * find the exception object to be rethrown.
 */
public class LoadExceptionObjectSnippets implements Snippets {

    @Snippet
    public static Object loadException(@ConstantParameter Register threadRegister) {
        Word thread = registerAsWord(threadRegister);
        Object exception = readExceptionOop(thread);
        writeExceptionOop(thread, null);
        writeExceptionPc(thread, WordFactory.zero());
        return piCastToSnippetReplaceeStamp(exception);
    }

    public static class Templates extends AbstractTemplates {

        private final SnippetInfo loadException = snippet(LoadExceptionObjectSnippets.class, "loadException", EXCEPTION_OOP_LOCATION, EXCEPTION_PC_LOCATION);
        private final HotSpotWordTypes wordTypes;

        public Templates(OptionValues options, Iterable<DebugHandlersFactory> factories, HotSpotProviders providers, TargetDescription target) {
            super(options, factories, providers, providers.getSnippetReflection(), target);
            this.wordTypes = providers.getWordTypes();
        }

        public void lower(LoadExceptionObjectNode loadExceptionObject, HotSpotRegistersProvider registers, LoweringTool tool) {
            StructuredGraph graph = loadExceptionObject.graph();
            if (LoadExceptionObjectInVM.getValue(graph.getOptions())) {
                ResolvedJavaType wordType = providers.getMetaAccess().lookupJavaType(Word.class);
                Stamp stamp = wordTypes.getWordStamp(wordType);
                ReadRegisterNode thread = graph.add(new ReadRegisterNode(stamp, registers.getThreadRegister(), true, false));
                graph.addBeforeFixed(loadExceptionObject, thread);
                ForeignCallNode loadExceptionC = graph.add(new ForeignCallNode(providers.getForeignCalls(), LOAD_AND_CLEAR_EXCEPTION, thread));
                loadExceptionC.setStateAfter(loadExceptionObject.stateAfter());
                graph.replaceFixedWithFixed(loadExceptionObject, loadExceptionC);
            } else {
                Arguments args = new Arguments(loadException, loadExceptionObject.graph().getGuardsStage(), tool.getLoweringStage());
                args.addConst("threadRegister", registers.getThreadRegister());
                template(loadExceptionObject, args).instantiate(providers.getMetaAccess(), loadExceptionObject, DEFAULT_REPLACER, args);
            }
        }
    }
}