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

import jdk.vm.ci.meta.Value;
import org.graalvm.compiler.core.common.spi.ForeignCallsProvider;
import org.graalvm.compiler.hotspot.stubs.Stub;

import java.util.List;

/**
 * HotSpot extension of {@link ForeignCallsProvider}.
 */
public interface HotSpotForeignCallsProvider extends ForeignCallsProvider {

    /**
     * Gets the registers that must be saved across a foreign call into the runtime.
     */
    Value[] getNativeABICallerSaveRegisters();

    /**
     * Gets the set of stubs linked to by the foreign calls represented by this object.
     */
    List<Stub> getStubs();
}
