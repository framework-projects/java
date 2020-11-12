/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.nodes.spi;

import org.graalvm.compiler.core.common.spi.ConstantFieldProvider;
import org.graalvm.compiler.core.common.spi.ForeignCallsProvider;

import jdk.vm.ci.meta.ConstantReflectionProvider;
import jdk.vm.ci.meta.MetaAccessProvider;

public class CoreProvidersImpl implements CoreProviders {
    protected final MetaAccessProvider metaAccess;
    protected final ConstantReflectionProvider constantReflection;
    protected final ConstantFieldProvider constantFieldProvider;
    protected final LoweringProvider lowerer;
    protected final Replacements replacements;
    protected final StampProvider stampProvider;
    protected final ForeignCallsProvider foreignCalls;
    protected final GCProvider gc;

    protected CoreProvidersImpl(MetaAccessProvider metaAccess, ConstantReflectionProvider constantReflection, ConstantFieldProvider constantFieldProvider, LoweringProvider lowerer,
                    Replacements replacements, StampProvider stampProvider, ForeignCallsProvider foreignCalls, GCProvider gc) {
        this.metaAccess = metaAccess;
        this.constantReflection = constantReflection;
        this.constantFieldProvider = constantFieldProvider;
        this.lowerer = lowerer;
        this.replacements = replacements;
        this.stampProvider = stampProvider;
        this.foreignCalls = foreignCalls;
        this.gc = gc;
    }

    @Override
    public MetaAccessProvider getMetaAccess() {
        return metaAccess;
    }

    @Override
    public ConstantReflectionProvider getConstantReflection() {
        return constantReflection;
    }

    @Override
    public ConstantFieldProvider getConstantFieldProvider() {
        return constantFieldProvider;
    }

    @Override
    public LoweringProvider getLowerer() {
        return lowerer;
    }

    @Override
    public Replacements getReplacements() {
        return replacements;
    }

    @Override
    public StampProvider getStampProvider() {
        return stampProvider;
    }

    @Override
    public ForeignCallsProvider getForeignCalls() {
        return foreignCalls;
    }

    @Override
    public GCProvider getGC() {
        return gc;
    }
}
