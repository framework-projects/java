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


package org.graalvm.compiler.asm.test;

import jdk.vm.ci.code.*;
import jdk.vm.ci.meta.MetaAccessProvider;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import jdk.vm.ci.runtime.JVMCI;
import jdk.vm.ci.runtime.JVMCIBackend;
import org.graalvm.compiler.api.test.Graal;
import org.graalvm.compiler.code.CompilationResult;
import org.graalvm.compiler.code.DisassemblerProvider;
import org.graalvm.compiler.core.common.CompilationIdentifier;
import org.graalvm.compiler.core.gen.LIRGenerationProvider;
import org.graalvm.compiler.core.target.Backend;
import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.runtime.RuntimeProvider;
import org.graalvm.compiler.serviceprovider.GraalServices;
import org.graalvm.compiler.test.GraalTest;
import org.junit.Assert;

import java.lang.reflect.Method;

import static org.graalvm.compiler.core.common.CompilationRequestIdentifier.asCompilationRequest;

public abstract class AssemblerTest extends GraalTest {

    private final MetaAccessProvider metaAccess;
    protected final CodeCacheProvider codeCache;
    private final Backend backend;

    public interface CodeGenTest {
        byte[] generateCode(CompilationResult compResult, TargetDescription target, RegisterConfig registerConfig, CallingConvention cc);
    }

    /**
     * Gets the initial option values provided by the Graal runtime. These are option values
     * typically parsed from the command line.
     */
    public static OptionValues getInitialOptions() {
        return Graal.getRequiredCapability(OptionValues.class);
    }

    public AssemblerTest() {
        JVMCIBackend providers = JVMCI.getRuntime().getHostJVMCIBackend();
        this.metaAccess = providers.getMetaAccess();
        this.codeCache = providers.getCodeCache();
        this.backend = Graal.getRequiredCapability(RuntimeProvider.class).getHostBackend();
    }

    public MetaAccessProvider getMetaAccess() {
        return metaAccess;
    }

    @SuppressWarnings("try")
    protected InstalledCode assembleMethod(Method m, CodeGenTest test) {
        ResolvedJavaMethod method = getMetaAccess().lookupJavaMethod(m);
        OptionValues options = getInitialOptions();
        DebugContext debug = getDebugContext(options);
        try (DebugContext.Scope s = debug.scope("assembleMethod", method, codeCache)) {
            RegisterConfig registerConfig = codeCache.getRegisterConfig();
            CompilationIdentifier compilationId = backend.getCompilationIdentifier(method);
            StructuredGraph graph = new StructuredGraph.Builder(options, debug).method(method).compilationId(compilationId).build();
            CallingConvention cc = ((LIRGenerationProvider) backend).newLIRGenerationResult(compilationId, null, null, graph, null).getCallingConvention();

            CompilationResult compResult = new CompilationResult(graph.compilationId());
            byte[] targetCode = test.generateCode(compResult, codeCache.getTarget(), registerConfig, cc);
            compResult.setTargetCode(targetCode, targetCode.length);
            compResult.setTotalFrameSize(0);
            compResult.close();

            InstalledCode code = backend.addInstalledCode(debug, method, asCompilationRequest(compilationId), compResult);

            for (DisassemblerProvider dis : GraalServices.load(DisassemblerProvider.class)) {
                String disasm1 = dis.disassembleCompiledCode(codeCache, compResult);
                Assert.assertTrue(compResult.toString(), disasm1 == null || disasm1.length() > 0);
                String disasm2 = dis.disassembleInstalledCode(codeCache, compResult, code);
                Assert.assertTrue(code.toString(), disasm2 == null || disasm2.length() > 0);
            }
            return code;
        } catch (Throwable e) {
            throw debug.handle(e);
        }
    }

    protected Object runTest(String methodName, CodeGenTest test, Object... args) {
        Method method = getMethod(methodName);
        InstalledCode code = assembleMethod(method, test);
        try {
            return code.executeVarargs(args);
        } catch (InvalidInstalledCodeException e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertReturn(String methodName, CodeGenTest test, Object expected, Object... args) {
        Object actual = runTest(methodName, test, args);
        Assert.assertEquals("unexpected return value", expected, actual);
    }
}
