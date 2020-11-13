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


package org.graalvm.compiler.hotspot.aarch64;

import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.RegisterArray;
import jdk.vm.ci.code.RegisterConfig;
import org.graalvm.compiler.core.common.alloc.RegisterAllocationConfig;

import java.util.ArrayList;
import java.util.BitSet;

import static jdk.vm.ci.aarch64.AArch64.*;

public class AArch64HotSpotRegisterAllocationConfig extends RegisterAllocationConfig {

    // @formatter:off
    static final Register[] registerAllocationOrder = {
        r0,  r1,  r2,  r3,  r4,  r5,  r6,  r7,
        r8,  r9,  r10, r11, r12, r13, r14, r15,
        r16, r17, r18, r19, r20, r21, r22, r23,
        r24, r25, r26, /* r27, */ r28, /* r29, r30, r31 */

        v0,  v1,  v2,  v3,  v4,  v5,  v6,  v7,
        v8,  v9,  v10, v11, v12, v13, v14, v15,
        v16, v17, v18, v19, v20, v21, v22, v23,
        v24, v25, v26, v27, v28, v29, v30, v31
    };
    // @formatter:on

    public AArch64HotSpotRegisterAllocationConfig(RegisterConfig registerConfig, String[] allocationRestrictedTo) {
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
