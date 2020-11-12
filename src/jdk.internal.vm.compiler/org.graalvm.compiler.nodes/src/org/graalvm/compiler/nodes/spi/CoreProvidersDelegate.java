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

public class CoreProvidersDelegate implements CoreProviders {

    private final CoreProviders providers;

    protected CoreProvidersDelegate(CoreProviders providers) {
        this.providers = providers;
    }

    @Override
    public MetaAccessProvider getMetaAccess() {
        return providers.getMetaAccess();
    }

    @Override
    public ConstantReflectionProvider getConstantReflection() {
        return providers.getConstantReflection();
    }

    @Override
    public ConstantFieldProvider getConstantFieldProvider() {
        return providers.getConstantFieldProvider();
    }

    @Override
    public LoweringProvider getLowerer() {
        return providers.getLowerer();
    }

    @Override
    public Replacements getReplacements() {
        return providers.getReplacements();
    }

    @Override
    public StampProvider getStampProvider() {
        return providers.getStampProvider();
    }

    @Override
    public ForeignCallsProvider getForeignCalls() {
        return providers.getForeignCalls();
    }

    @Override
    public GCProvider getGC() {
        return providers.getGC();
    }
}
