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



package jdk.tools.jaotc;

import jdk.vm.ci.hotspot.HotSpotJVMCIRuntime;
import jdk.vm.ci.hotspot.HotSpotResolvedJavaMethod;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import jdk.vm.ci.runtime.JVMCICompiler;
import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.code.CompilationResult;
import org.graalvm.compiler.core.GraalCompilerOptions;
import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.debug.DebugContext.Activation;
import org.graalvm.compiler.debug.TTY;
import org.graalvm.compiler.options.OptionValues;
import org.graalvm.compiler.printer.GraalDebugHandlersFactory;
import org.graalvm.compiler.serviceprovider.GraalServices;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a task in the compile queue.
 *
 * This class encapsulates all Graal-specific information that is used during offline AOT
 * compilation of classes. It also defines methods that parse compilation result of Graal to create
 * target-independent representation {@code BinaryContainer} of the intended target binary.
 */
final class AOTCompilationTask implements Runnable, Comparable<Object> {

    private static final AtomicInteger ids = new AtomicInteger();

    private final Main main;

    private OptionValues graalOptions;

    /**
     * The compilation id of this task.
     */
    private final int id;

    private final AOTCompiledClass holder;

    /**
     * Method this task represents.
     */
    private final ResolvedJavaMethod method;

    private final AOTBackend aotBackend;

    /**
     * The result of this compilation task.
     */
    private CompiledMethodInfo result;

    AOTCompilationTask(Main main, OptionValues graalOptions, AOTCompiledClass holder, ResolvedJavaMethod method, AOTBackend aotBackend) {
        this.main = main;
        this.graalOptions = graalOptions;
        this.id = ids.incrementAndGet();
        this.holder = holder;
        this.method = method;
        this.aotBackend = aotBackend;
    }

    /**
     * Compile a method or a constructor.
     */
    @Override
    @SuppressWarnings("try")
    public void run() {
        // Ensure a JVMCI runtime is initialized prior to Debug being initialized as the former
        // may include processing command line options used by the latter.
        HotSpotJVMCIRuntime.runtime();

        AOTCompiler.logCompilation(JavaMethodInfo.uniqueMethodName(method), "Compiling");

        final long threadId = Thread.currentThread().getId();

        final boolean printCompilation = GraalCompilerOptions.PrintCompilation.getValue(graalOptions) && !TTY.isSuppressed() && GraalServices.isThreadAllocatedMemorySupported();
        if (printCompilation) {
            TTY.println(getMethodDescription() + "...");
        }

        final long start;
        final long allocatedBytesBefore;
        if (printCompilation) {
            start = System.currentTimeMillis();
            allocatedBytesBefore = GraalServices.getThreadAllocatedBytes(threadId);
        } else {
            start = 0L;
            allocatedBytesBefore = 0L;
        }

        CompilationResult compResult = null;
        final long startTime = System.currentTimeMillis();
        SnippetReflectionProvider snippetReflection = aotBackend.getProviders().getSnippetReflection();
        try (DebugContext debug = DebugContext.create(graalOptions, new GraalDebugHandlersFactory(snippetReflection)); Activation a = debug.activate()) {
            compResult = aotBackend.compileMethod(method, debug);
        }
        final long endTime = System.currentTimeMillis();

        if (printCompilation) {
            final long stop = System.currentTimeMillis();
            final int targetCodeSize = compResult != null ? compResult.getTargetCodeSize() : -1;
            final long allocatedBytesAfter = GraalServices.getThreadAllocatedBytes(threadId);
            final long allocatedBytes = (allocatedBytesAfter - allocatedBytesBefore) / 1024;

            TTY.println(getMethodDescription() + String.format(" | %4dms %5dB %5dkB", stop - start, targetCodeSize, allocatedBytes));
        }

        if (compResult == null) {
            result = null;
            return;
        }

        // For now precision to the nearest second is sufficient.
        LogPrinter.writeLog("    Compile Time: " + TimeUnit.MILLISECONDS.toSeconds(endTime - startTime) + "secs");
        if (main.options.debug) {
            aotBackend.printCompiledMethod((HotSpotResolvedJavaMethod) method, compResult);
        }

        result = new CompiledMethodInfo(compResult, new AOTHotSpotResolvedJavaMethod((HotSpotResolvedJavaMethod) method, aotBackend.getBackend(), graalOptions));
    }

    private String getMethodDescription() {
        return String.format("%-6d aot %s %s", getId(), JavaMethodInfo.uniqueMethodName(method),
                        getEntryBCI() == JVMCICompiler.INVOCATION_ENTRY_BCI ? "" : "(OSR@" + getEntryBCI() + ") ");
    }

    private int getId() {
        return id;
    }

    private static int getEntryBCI() {
        return JVMCICompiler.INVOCATION_ENTRY_BCI;
    }

    ResolvedJavaMethod getMethod() {
        return method;
    }

    /**
     * Returns the holder of this method as a {@link AOTCompiledClass}.
     *
     * @return the holder of this method
     */
    AOTCompiledClass getHolder() {
        return holder;
    }

    /**
     * Returns the result of this compilation task.
     *
     * @return result of this compilation task
     */
    CompiledMethodInfo getResult() {
        return result;
    }

    @Override
    public int compareTo(Object obj) {
        AOTCompilationTask other = (AOTCompilationTask) obj;
        return this.id - other.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AOTCompilationTask other = (AOTCompilationTask) obj;
        return (this.id == other.id);
    }

    @Override
    public int hashCode() {
        return 31 + id;
    }

}
