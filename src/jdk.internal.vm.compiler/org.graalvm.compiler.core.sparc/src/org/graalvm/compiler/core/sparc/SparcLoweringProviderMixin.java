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



package org.graalvm.compiler.core.sparc;

import org.graalvm.compiler.nodes.spi.LoweringProvider;

public interface SparcLoweringProviderMixin extends LoweringProvider {

    @Override
    default Integer smallestCompareWidth() {
        return 32;
    }

    @Override
    default boolean supportBulkZeroing() {
        return false;
    }
}
