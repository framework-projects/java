/*
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.test;

import org.graalvm.compiler.serviceprovider.JavaVersionUtil;

/**
 * A class loader that exports all packages in the module defining the class loader to all classes
 * in the unnamed module associated with the loader.
 */
public class ExportingClassLoader extends ClassLoader {
    public ExportingClassLoader() {
        if (JavaVersionUtil.JAVA_SPEC > 8) {
            JLModule.fromClass(getClass()).exportAllPackagesTo(JLModule.getUnnamedModuleFor(this));
        }
    }

    public ExportingClassLoader(ClassLoader parent) {
        super(parent);
        if (JavaVersionUtil.JAVA_SPEC > 8) {
            JLModule.fromClass(getClass()).exportAllPackagesTo(JLModule.getUnnamedModuleFor(this));
        }
    }
}
