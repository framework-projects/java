/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
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
 *
 *
 */

package com.sun.tools.javap; //javax.tools;

import javax.tools.*;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * This class is intended to be put in javax.tools.
 *
 * @see DiagnosticListener
 * @see Diagnostic
 * @see JavaFileManager
 * @since 1.7
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public interface DisassemblerTool extends Tool, OptionChecker {

    /**
     * Creates a future for a disassembly task with the given
     * components and arguments.  The task might not have
     * completed as described in the DissemblerTask interface.
     *
     * <p>If a file manager is provided, it must be able to handle all
     * locations defined in {@link StandardLocation}.
     *
     * @param out a Writer for additional output from the compiler;
     * use {@code System.err} if {@code null}
     * @param fileManager a file manager; if {@code null} use the
     * compiler's standard filemanager
     * @param diagnosticListener a diagnostic listener; if {@code
     * null} use the compiler's default method for reporting
     * diagnostics
     * @param options compiler options, {@code null} means no options
     * @param classes class names (for annotation processing), {@code
     * null} means no class names
     * @return a task to perform the disassembly
     * @throws RuntimeException if an unrecoverable error
     * occurred in a user supplied component.  The
     * {@linkplain Throwable#getCause() cause} will be the error in
     * user code.
     * @throws IllegalArgumentException if any of the given
     * compilation units are of other kind than
     * {@linkplain JavaFileObject.Kind#SOURCE source}
     */
    DisassemblerTask getTask(Writer out,
                            JavaFileManager fileManager,
                            DiagnosticListener<? super JavaFileObject> diagnosticListener,
                            Iterable<String> options,
                            Iterable<String> classes);

    /**
     * Returns a new instance of the standard file manager implementation
     * for this tool.  The file manager will use the given diagnostic
     * listener for producing any non-fatal diagnostics.  Fatal errors
     * will be signalled with the appropriate exceptions.
     *
     * <p>The standard file manager will be automatically reopened if
     * it is accessed after calls to {@code flush} or {@code close}.
     * The standard file manager must be usable with other tools.
     *
     * @param diagnosticListener a diagnostic listener for non-fatal
     * diagnostics; if {@code null} use the compiler's default method
     * for reporting diagnostics
     * @param locale the locale to apply when formatting diagnostics;
     * {@code null} means the {@linkplain Locale#getDefault() default locale}.
     * @param charset the character set used for decoding bytes; if
     * {@code null} use the platform default
     * @return the standard file manager
     */
    StandardJavaFileManager getStandardFileManager(
        DiagnosticListener<? super JavaFileObject> diagnosticListener,
        Locale locale,
        Charset charset);

    /**
     * Interface representing a future for a disassembly task.  The
     * task has not yet started.  To start the task, call
     * the {@linkplain #call call} method.
     *
     * <p>Before calling the call method, additional aspects of the
     * task can be configured, for example, by calling the
     * {@linkplain #setLocale setLocale} method.
     */
    interface DisassemblerTask extends Callable<Boolean> {

        /**
         * Set the locale to be applied when formatting diagnostics and
         * other localized data.
         *
         * @param locale the locale to apply; {@code null} means apply no
         * locale
         * @throws IllegalStateException if the task has started
         */
        void setLocale(Locale locale);

        /**
         * Performs this compilation task.  The compilation may only
         * be performed once.  Subsequent calls to this method throw
         * IllegalStateException.
         *
         * @return true if and only all the files compiled without errors;
         * false otherwise
         *
         * @throws RuntimeException if an unrecoverable error occurred
         * in a user-supplied component.  The
         * {@linkplain Throwable#getCause() cause} will be the error
         * in user code.
         * @throws IllegalStateException if called more than once
         */
        Boolean call();
    }
}