/*
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.lir.gen;

import jdk.vm.ci.meta.Value;
import org.graalvm.compiler.core.common.cfg.AbstractBlockBase;

public interface BlockValueMap {

    void accessOperand(Value operand, AbstractBlockBase<?> block);

    void defineOperand(Value operand, AbstractBlockBase<?> block);

}
