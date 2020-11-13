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


package org.graalvm.compiler.hotspot.sparc;

import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.RegisterArray;
import jdk.vm.ci.code.RegisterConfig;
import org.graalvm.compiler.core.common.alloc.RegisterAllocationConfig;

import java.util.ArrayList;
import java.util.BitSet;

import static jdk.vm.ci.sparc.SPARC.*;

public class SPARCHotSpotRegisterAllocationConfig extends RegisterAllocationConfig {

    // @formatter:off
    static final Register[] registerAllocationOrder = {
      l0, l1, l2, l3, l4, l5, l6, l7,
      i0, i1, i2, i3, i4, i5, /*i6,*/ /*i7,*/
      o0, o1, o2, o3, o4, o5, /*o6, o7,*/
      g1, g4, g5,
      // f0, f1, f2, f3, f4, f5, f6, f7
      f8,  f9,  f10, f11, f12, f13, f14, f15,
      f16, f17, f18, f19, f20, f21, f22, f23,
      f24, f25, f26, f27, f28, f29, f30, f31,
      d32, d34, d36, d38, d40, d42, d44, d46,
      d48, d50, d52, d54, d56, d58, d60, d62
    };
    // @formatter:on

    public SPARCHotSpotRegisterAllocationConfig(RegisterConfig registerConfig, String[] allocationRestrictedTo) {
        super(registerConfig, allocationRestrictedTo);
    }

    @Override
    protected RegisterArray initAllocatable(RegisterArray registers) {
        BitSet regMap = new BitSet(registerConfig.getAllocatableRegisters().size());
        for (Register reg : registers) {
            regMap.set(reg.number);
        }

        ArrayList<Register> allocatableRegisters = new ArrayList<>(registers.size());
        for (Register reg : registerAllocationOrder) {
            if (regMap.get(reg.number)) {
                allocatableRegisters.add(reg);
            }
        }

        return super.initAllocatable(new RegisterArray(allocatableRegisters));
    }
}
