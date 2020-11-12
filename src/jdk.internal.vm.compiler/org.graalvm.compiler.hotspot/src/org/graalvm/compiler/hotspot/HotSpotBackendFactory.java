/*
 * Copyright (c) 2013, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot;

import org.graalvm.compiler.bytecode.BytecodeProvider;
import org.graalvm.compiler.hotspot.meta.HotSpotGCProvider;
import org.graalvm.compiler.hotspot.meta.HotSpotGraalConstantFieldProvider;
import org.graalvm.compiler.hotspot.meta.HotSpotSnippetReflectionProvider;
import org.graalvm.compiler.hotspot.meta.HotSpotStampProvider;
import org.graalvm.compiler.hotspot.word.HotSpotWordTypes;
import org.graalvm.compiler.phases.tiers.CompilerConfiguration;
import org.graalvm.compiler.phases.util.Providers;
import org.graalvm.compiler.replacements.classfile.ClassfileBytecodeProvider;

import jdk.vm.ci.code.Architecture;
import jdk.vm.ci.code.TargetDescription;
import jdk.vm.ci.hotspot.HotSpotConstantReflectionProvider;
import jdk.vm.ci.hotspot.HotSpotJVMCIRuntime;
import jdk.vm.ci.hotspot.HotSpotMetaAccessProvider;

public abstract class HotSpotBackendFactory {

    protected HotSpotGraalConstantFieldProvider createConstantFieldProvider(GraalHotSpotVMConfig config, HotSpotMetaAccessProvider metaAccess) {
        return new HotSpotGraalConstantFieldProvider(config, metaAccess);
    }

    protected HotSpotWordTypes createWordTypes(HotSpotMetaAccessProvider metaAccess, TargetDescription target) {
        return new HotSpotWordTypes(metaAccess, target.wordJavaKind);
    }

    protected HotSpotStampProvider createStampProvider() {
        return new HotSpotStampProvider();
    }

    protected HotSpotGCProvider createGCProvider(GraalHotSpotVMConfig config) {
        return new HotSpotGCProvider(config);
    }

    protected HotSpotReplacementsImpl createReplacements(TargetDescription target, Providers p, HotSpotSnippetReflectionProvider snippetReflection, BytecodeProvider bytecodeProvider) {
        return new HotSpotReplacementsImpl(p, snippetReflection, bytecodeProvider, target);
    }

    protected ClassfileBytecodeProvider createBytecodeProvider(HotSpotMetaAccessProvider metaAccess, HotSpotSnippetReflectionProvider snippetReflection) {
        return new ClassfileBytecodeProvider(metaAccess, snippetReflection);
    }

    protected HotSpotSnippetReflectionProvider createSnippetReflection(HotSpotGraalRuntimeProvider runtime, HotSpotConstantReflectionProvider constantReflection, HotSpotWordTypes wordTypes) {
        return new HotSpotSnippetReflectionProvider(runtime, constantReflection, wordTypes);
    }

    /**
     * Gets the name of this backend factory. This should not include the {@link #getArchitecture()
     * architecture}. The {@link CompilerConfigurationFactory} can select alternative backends based
     * on this name.
     */
    public abstract String getName();

    /**
     * Gets the class describing the architecture the backend created by this factory is associated
     * with.
     */
    public abstract Class<? extends Architecture> getArchitecture();

    public abstract HotSpotBackend createBackend(HotSpotGraalRuntimeProvider runtime, CompilerConfiguration compilerConfiguration, HotSpotJVMCIRuntime jvmciRuntime, HotSpotBackend host);
}
