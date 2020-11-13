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


package org.graalvm.compiler.hotspot;

import jdk.vm.ci.hotspot.HotSpotMetaData;

/**
 * JDK 13 version of {@code HotSpotGraalServices}.
 */
public class HotSpotGraalServices {

    /**
     * Get the implicit exceptions section of a {@code HotSpotMetaData} if it exists.
     */
    @SuppressWarnings("unused")
    public static byte[] getImplicitExceptionBytes(HotSpotMetaData metaData) {
        return metaData.implicitExceptionBytes();
    }

    public static CompilationContext enterGlobalCompilationContext() {
        return null;
    }

    @SuppressWarnings("unused")
    public static CompilationContext openLocalCompilationContext(Object description) {
        return null;
    }

    public static void exit(int status) {
        System.exit(status);
    }
}
