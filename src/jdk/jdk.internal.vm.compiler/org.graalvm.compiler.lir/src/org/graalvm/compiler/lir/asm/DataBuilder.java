/*
 * Copyright (c) 2015, 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.lir.asm;

import jdk.vm.ci.meta.Constant;
import org.graalvm.compiler.code.DataSection.Data;

public abstract class DataBuilder {
    public abstract Data createDataItem(Constant c);
}
