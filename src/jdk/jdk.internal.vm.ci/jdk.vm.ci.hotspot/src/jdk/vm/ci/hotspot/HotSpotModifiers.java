/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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
package jdk.vm.ci.hotspot;

import java.lang.reflect.Modifier;

import static java.lang.reflect.Modifier.*;
import static jdk.vm.ci.hotspot.HotSpotVMConfig.config;

/**
 * The non-public modifiers in {@link Modifier} that need to be retrieved from
 * {@link HotSpotVMConfig}.
 */
public class HotSpotModifiers {

    // @formatter:off
    public static final int ANNOTATION = config().jvmAccAnnotation;
    public static final int ENUM       = config().jvmAccEnum;
    public static final int VARARGS    = config().jvmAccVarargs;
    public static final int BRIDGE     = config().jvmAccBridge;
    public static final int SYNTHETIC  = config().jvmAccSynthetic;
    // @formatter:on

    public static int jvmClassModifiers() {
        return PUBLIC | FINAL | INTERFACE | ABSTRACT | ANNOTATION | ENUM | SYNTHETIC;
    }

    public static int jvmMethodModifiers() {
        return PUBLIC | PRIVATE | PROTECTED | STATIC | FINAL | SYNCHRONIZED | BRIDGE | VARARGS | NATIVE | ABSTRACT | STRICT | SYNTHETIC;
    }

    public static int jvmFieldModifiers() {
        return PUBLIC | PRIVATE | PROTECTED | STATIC | FINAL | VOLATILE | TRANSIENT | ENUM | SYNTHETIC;
    }
}
