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


package org.graalvm.compiler.hotspot.meta;

import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.core.common.spi.ConstantFieldProvider;
import org.graalvm.compiler.core.common.spi.ForeignCallsProvider;
import org.graalvm.compiler.hotspot.word.HotSpotWordTypes;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderConfiguration.Plugins;
import org.graalvm.compiler.nodes.spi.LoweringProvider;
import org.graalvm.compiler.nodes.spi.Replacements;
import org.graalvm.compiler.phases.tiers.SuitesProvider;
import org.graalvm.compiler.phases.util.Providers;

import jdk.vm.ci.code.CodeCacheProvider;
import jdk.vm.ci.hotspot.HotSpotCodeCacheProvider;
import jdk.vm.ci.meta.ConstantReflectionProvider;
import jdk.vm.ci.meta.MetaAccessProvider;

/**
 * Extends {@link Providers} to include a number of extra capabilities used by the HotSpot parts of
 * the compiler.
 */
public class HotSpotProviders extends Providers {

    private final SuitesProvider suites;
    private final HotSpotRegistersProvider registers;
    private final SnippetReflectionProvider snippetReflection;
    private final HotSpotWordTypes wordTypes;
    private final Plugins graphBuilderPlugins;
    private final HotSpotGCProvider gc;

    public HotSpotProviders(MetaAccessProvider metaAccess, HotSpotCodeCacheProvider codeCache, ConstantReflectionProvider constantReflection, ConstantFieldProvider constantField,
                    HotSpotForeignCallsProvider foreignCalls, LoweringProvider lowerer, Replacements replacements, SuitesProvider suites, HotSpotRegistersProvider registers,
                    SnippetReflectionProvider snippetReflection, HotSpotWordTypes wordTypes, Plugins graphBuilderPlugins, HotSpotGCProvider gc) {
        super(metaAccess, codeCache, constantReflection, constantField, foreignCalls, lowerer, replacements, new HotSpotStampProvider(), gc);
        this.suites = suites;
        this.registers = registers;
        this.snippetReflection = snippetReflection;
        this.wordTypes = wordTypes;
        this.graphBuilderPlugins = graphBuilderPlugins;
        this.gc = gc;
    }

    @Override
    public HotSpotCodeCacheProvider getCodeCache() {
        return (HotSpotCodeCacheProvider) super.getCodeCache();
    }

    @Override
    public HotSpotForeignCallsProvider getForeignCalls() {
        return (HotSpotForeignCallsProvider) super.getForeignCalls();
    }

    public SuitesProvider getSuites() {
        return suites;
    }

    public HotSpotRegistersProvider getRegisters() {
        return registers;
    }

    public SnippetReflectionProvider getSnippetReflection() {
        return snippetReflection;
    }

    public Plugins getGraphBuilderPlugins() {
        return graphBuilderPlugins;
    }

    public HotSpotWordTypes getWordTypes() {
        return wordTypes;
    }

    @Override
    public HotSpotGCProvider getGC() {
        return gc;
    }

    @Override
    public Providers copyWith(MetaAccessProvider substitution) {
        return new HotSpotProviders(substitution, getCodeCache(), getConstantReflection(), getConstantFieldProvider(), getForeignCalls(), getLowerer(), getReplacements(), getSuites(),
                        getRegisters(), getSnippetReflection(), getWordTypes(), getGraphBuilderPlugins(), getGC());
    }

    @Override
    public Providers copyWith(CodeCacheProvider substitution) {
        return new HotSpotProviders(getMetaAccess(), (HotSpotCodeCacheProvider) substitution, getConstantReflection(), getConstantFieldProvider(), getForeignCalls(), getLowerer(), getReplacements(),
                        getSuites(), getRegisters(), getSnippetReflection(), getWordTypes(), getGraphBuilderPlugins(), getGC());
    }

    @Override
    public Providers copyWith(ConstantReflectionProvider substitution) {
        return new HotSpotProviders(getMetaAccess(), getCodeCache(), substitution, getConstantFieldProvider(), getForeignCalls(), getLowerer(), getReplacements(), getSuites(),
                        getRegisters(), getSnippetReflection(), getWordTypes(), getGraphBuilderPlugins(), getGC());
    }

    @Override
    public Providers copyWith(ConstantFieldProvider substitution) {
        return new HotSpotProviders(getMetaAccess(), getCodeCache(), getConstantReflection(), substitution, getForeignCalls(), getLowerer(), getReplacements(), getSuites(),
                        getRegisters(), getSnippetReflection(), getWordTypes(), getGraphBuilderPlugins(), getGC());
    }

    @Override
    public Providers copyWith(ForeignCallsProvider substitution) {
        return new HotSpotProviders(getMetaAccess(), getCodeCache(), getConstantReflection(), getConstantFieldProvider(), (HotSpotForeignCallsProvider) substitution, getLowerer(), getReplacements(),
                        getSuites(), getRegisters(), getSnippetReflection(), getWordTypes(), getGraphBuilderPlugins(), getGC());
    }

    @Override
    public Providers copyWith(LoweringProvider substitution) {
        return new HotSpotProviders(getMetaAccess(), getCodeCache(), getConstantReflection(), getConstantFieldProvider(), getForeignCalls(), substitution, getReplacements(), getSuites(),
                        getRegisters(), getSnippetReflection(), getWordTypes(), getGraphBuilderPlugins(), getGC());
    }

    @Override
    public Providers copyWith(Replacements substitution) {
        return new HotSpotProviders(getMetaAccess(), getCodeCache(), getConstantReflection(), getConstantFieldProvider(), getForeignCalls(), getLowerer(), substitution, getSuites(),
                        getRegisters(), getSnippetReflection(), getWordTypes(), getGraphBuilderPlugins(), getGC());
    }

    public Providers copyWith(Plugins substitution) {
        return new HotSpotProviders(getMetaAccess(), getCodeCache(), getConstantReflection(), getConstantFieldProvider(), getForeignCalls(), getLowerer(), getReplacements(), getSuites(),
                        getRegisters(), getSnippetReflection(), getWordTypes(), substitution, getGC());
    }
}
