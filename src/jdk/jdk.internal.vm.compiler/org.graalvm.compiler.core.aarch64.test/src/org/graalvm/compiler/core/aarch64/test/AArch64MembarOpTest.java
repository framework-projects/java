/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, Arm Limited. All rights reserved.
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


package org.graalvm.compiler.core.aarch64.test;

import jdk.internal.vm.compiler.collections.Pair;
import jdk.vm.ci.aarch64.AArch64;
import jdk.vm.ci.code.MemoryBarriers;
import jdk.vm.ci.runtime.JVMCI;
import jdk.vm.ci.runtime.JVMCIBackend;
import org.graalvm.compiler.asm.aarch64.AArch64Assembler.BarrierKind;
import org.graalvm.compiler.asm.aarch64.AArch64MacroAssembler;
import org.graalvm.compiler.code.CompilationResult;
import org.graalvm.compiler.core.gen.LIRGenerationProvider;
import org.graalvm.compiler.core.test.backend.BackendTest;
import org.graalvm.compiler.lir.aarch64.AArch64Move.MembarOp;
import org.graalvm.compiler.lir.asm.CompilationResultBuilder;
import org.graalvm.compiler.lir.asm.CompilationResultBuilderFactory;
import org.graalvm.compiler.lir.gen.LIRGenerationResult;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeTrue;

public class AArch64MembarOpTest extends BackendTest {

    private final JVMCIBackend providers;
    private final CompilationResultBuilder crb;

    public AArch64MembarOpTest() {
        this.providers = JVMCI.getRuntime().getHostJVMCIBackend();

        final StructuredGraph graph = parseEager("stub", StructuredGraph.AllowAssumptions.YES);
        LIRGenerationResult lirGenRes = getLIRGenerationResult(graph);
        CompilationResult compResult = new CompilationResult(graph.compilationId());
        this.crb = ((LIRGenerationProvider) getBackend()).newCompilationResultBuilder(lirGenRes, lirGenRes.getFrameMap(), compResult, CompilationResultBuilderFactory.Default);
    }

    public void stub() {
    }

    @Before
    public void checkAArch64() {
        assumeTrue("skipping AArch64 specific test", JVMCI.getRuntime().getHostJVMCIBackend().getTarget().arch instanceof AArch64);
    }

    @Test
    public void runNormalMembarTests() {
        List<Pair<Integer, BarrierKind>> cases = new ArrayList<>();
        cases.add(Pair.create(MemoryBarriers.LOAD_LOAD, BarrierKind.LOAD_LOAD));
        cases.add(Pair.create(MemoryBarriers.LOAD_STORE, BarrierKind.LOAD_LOAD));
        cases.add(Pair.create(MemoryBarriers.LOAD_LOAD | MemoryBarriers.LOAD_STORE, BarrierKind.LOAD_LOAD));
        cases.add(Pair.create(MemoryBarriers.STORE_LOAD, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_LOAD | MemoryBarriers.LOAD_LOAD, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_LOAD | MemoryBarriers.LOAD_STORE, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_LOAD | MemoryBarriers.LOAD_LOAD | MemoryBarriers.LOAD_STORE, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_STORE, BarrierKind.STORE_STORE));
        cases.add(Pair.create(MemoryBarriers.STORE_STORE | MemoryBarriers.LOAD_LOAD, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_STORE | MemoryBarriers.LOAD_STORE, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_STORE | MemoryBarriers.LOAD_LOAD | MemoryBarriers.LOAD_STORE, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_STORE | MemoryBarriers.STORE_LOAD, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_STORE | MemoryBarriers.STORE_LOAD | MemoryBarriers.LOAD_LOAD, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_STORE | MemoryBarriers.STORE_LOAD | MemoryBarriers.LOAD_STORE, BarrierKind.ANY_ANY));
        cases.add(Pair.create(MemoryBarriers.STORE_STORE | MemoryBarriers.STORE_LOAD | MemoryBarriers.LOAD_STORE | MemoryBarriers.LOAD_LOAD, BarrierKind.ANY_ANY));

        for (Pair<Integer, BarrierKind> c : cases) {
            assertArrayEquals(new MembarOpActual(c.getLeft()).emit(new AArch64MacroAssembler(providers.getTarget())),
                            new MembarOpExpected(c.getRight()).emit(new AArch64MacroAssembler(providers.getTarget())));
        }
    }

    @Test(expected = AssertionError.class)
    public void runExceptionalTests() {
        new MembarOpActual(16).emit(new AArch64MacroAssembler(providers.getTarget()));
    }

    private class MembarOpActual {
        private MembarOp op;

        MembarOpActual(int barriers) {
            op = new MembarOp(barriers);
        }

        byte[] emit(AArch64MacroAssembler masm) {
            op.emitCode(crb, masm);
            return masm.close(false);
        }
    }

    private class MembarOpExpected {
        private BarrierKind barrierKind;

        MembarOpExpected(BarrierKind barrierKind) {
            this.barrierKind = barrierKind;
        }

        byte[] emit(AArch64MacroAssembler masm) {
            masm.dmb(barrierKind);
            return masm.close(false);
        }
    }
}