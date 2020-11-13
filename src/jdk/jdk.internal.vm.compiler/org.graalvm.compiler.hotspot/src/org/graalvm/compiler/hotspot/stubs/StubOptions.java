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


package org.graalvm.compiler.hotspot.stubs;

import org.graalvm.compiler.options.Option;
import org.graalvm.compiler.options.OptionKey;
import org.graalvm.compiler.options.OptionType;

//JaCoCo Exclude

/**
 * Options related to HotSpot Graal-generated stubs.
 *
 * Note: This must be a top level class to work around for
 * <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=477597">Eclipse bug 477597</a>.
 */
public class StubOptions {
    // @formatter:off
    @Option(help = "Trace execution of stub used to handle an exception thrown by a callee.", type = OptionType.Debug)
    static final OptionKey<Boolean> TraceExceptionHandlerStub = new OptionKey<>(false);

    @Option(help = "Trace execution of the stub that routes an exception to a handler in the calling frame.", type = OptionType.Debug)
    static final OptionKey<Boolean> TraceUnwindStub = new OptionKey<>(false);
    //@formatter:on
}
