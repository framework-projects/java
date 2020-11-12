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

public interface CoreProviders {

    MetaAccessProvider getMetaAccess();

    ConstantReflectionProvider getConstantReflection();

    ConstantFieldProvider getConstantFieldProvider();

    LoweringProvider getLowerer();

    Replacements getReplacements();

    StampProvider getStampProvider();

    ForeignCallsProvider getForeignCalls();

    GCProvider getGC();
}
