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


package org.graalvm.compiler.hotspot.amd64;

import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.RegisterArray;
import jdk.vm.ci.code.RegisterConfig;
import org.graalvm.compiler.core.common.alloc.RegisterAllocationConfig;

import java.util.ArrayList;
import java.util.BitSet;

import static jdk.vm.ci.amd64.AMD64.*;

class AMD64HotSpotRegisterAllocationConfig extends RegisterAllocationConfig {
    /**
     * Specify priority of register selection within phases of register allocation. Highest priority
     * is first. A useful heuristic is to give registers a low priority when they are required by
     * machine instructions, like EAX and EDX on I486, and choose no-save registers before
     * save-on-call, & save-on-call before save-on-entry. Registers which participate in fixed
     * calling sequences should come last. Registers which are used as pairs must fall on an even
     * boundary.
     *
     * Adopted from x86_64.ad.
     */
    // @formatter:off
    static final Register[] registerAllocationOrder = {
        r10, r11, r8, r9, r12, rcx, rbx, rdi, rdx, rsi, rax, rbp, r13, r14, /*r15,*/ /*rsp,*/
        xmm0, xmm1, xmm2,  xmm3,  xmm4,  xmm5,  xmm6,  xmm7,
        xmm8, xmm9, xmm10, xmm11, xmm12, xmm13, xmm14, xmm15
    };
    // @formatter:on

    AMD64HotSpotRegisterAllocationConfig(RegisterConfig registerConfig, String[] allocationRestrictedTo) {
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
