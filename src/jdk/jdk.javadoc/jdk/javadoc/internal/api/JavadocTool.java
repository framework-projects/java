/*
 * Copyright (c) 2012, 2016, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.api;

import com.sun.tools.javac.api.ClientCodeWrapper;
import com.sun.tools.javac.file.BaseFileManager;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import jdk.javadoc.internal.tool.ToolOption;

import javax.lang.model.SourceVersion;
import javax.tools.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Provides access to functionality specific to the JDK documentation tool,
 * javadoc.
 *
 * <p><b>This is NOT part of any supported API.
 * If you write code that depends on this, you do so at your own
 * risk.  This code and its internal interfaces are subject to change
 * or deletion without notice.</b></p>
 */
public class JavadocTool implements DocumentationTool {
    // @Override // can't add @Override until bootstrap JDK provides Tool.name()
    public String name() {
        return "javadoc";
    }

    @Override
    public DocumentationTask getTask(
            Writer out,
            JavaFileManager fileManager,
            DiagnosticListener<? super JavaFileObject> diagnosticListener,
            Class<?> docletClass,
            Iterable<String> options,
            Iterable<? extends JavaFileObject> compilationUnits) {
        Context context = new Context();
        return getTask(out, fileManager, diagnosticListener,
                docletClass, options, compilationUnits, context);
    }

    public DocumentationTask getTask(
            Writer out,
            JavaFileManager fileManager,
            DiagnosticListener<? super JavaFileObject> diagnosticListener,
            Class<?> docletClass,
            Iterable<String> options,
            Iterable<? extends JavaFileObject> compilationUnits,
            Context context) {
        try {
            ClientCodeWrapper ccw = ClientCodeWrapper.instance(context);

            if (options != null) {
                for (String option : options)
                    Objects.requireNonNull(option);
            }

            if (compilationUnits != null) {
                compilationUnits = ccw.wrapJavaFileObjects(compilationUnits); // implicit null check
                for (JavaFileObject cu : compilationUnits) {
                    if (cu.getKind() != JavaFileObject.Kind.SOURCE) {
                        final String kindMsg = "All compilation units must be of SOURCE kind";
                        throw new IllegalArgumentException(kindMsg);
                    }
                }
            }

            if (diagnosticListener != null)
                context.put(DiagnosticListener.class, ccw.wrap(diagnosticListener));

            if (out == null)
                context.put(Log.errKey, new PrintWriter(System.err, true));
            else if (out instanceof PrintWriter)
                context.put(Log.errKey, ((PrintWriter) out));
            else
                context.put(Log.errKey, new PrintWriter(out, true));

            if (fileManager == null) {
                fileManager = getStandardFileManager(diagnosticListener, null, null);
                if (fileManager instanceof BaseFileManager) {
                    ((BaseFileManager) fileManager).autoClose = true;
                }
            }
            fileManager = ccw.wrap(fileManager);
            context.put(JavaFileManager.class, fileManager);

            return new JavadocTaskImpl(context, docletClass, options, compilationUnits);
        } catch (ClientCodeException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    // TODO: used shared static method in JavacFileManager
    @Override
    public StandardJavaFileManager getStandardFileManager(
            DiagnosticListener<? super JavaFileObject> diagnosticListener,
            Locale locale,
            Charset charset) {
        Context context = new Context();
        context.put(Locale.class, locale);
        if (diagnosticListener != null)
            context.put(DiagnosticListener.class, diagnosticListener);
        PrintWriter pw = (charset == null)
                ? new PrintWriter(System.err, true)
                : new PrintWriter(new OutputStreamWriter(System.err, charset), true);
        context.put(Log.errKey, pw);
        return new JavacFileManager(context, true, charset);
    }

    @Override
    public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
        PrintWriter err_pw = new PrintWriter(err == null ? System.err : err, true);
        PrintWriter out_pw = new PrintWriter(out == null ? System.out : out);
        try {
            return jdk.javadoc.internal.tool.Main.execute(arguments, err_pw);
        } finally {
            err_pw.flush();
            out_pw.flush();
        }
    }

    @Override
    public Set<SourceVersion> getSourceVersions() {
        return Collections.unmodifiableSet(
                EnumSet.range(SourceVersion.RELEASE_3, SourceVersion.latest()));
    }

    @Override
    public int isSupportedOption(String option) {
        if (option == null)
            throw new NullPointerException();
        for (ToolOption o : ToolOption.values()) {
            for (String name : o.names) {
                if (name.equals(option))
                    return o.hasArg ? 1 : 0;
            }
        }
        return -1;
    }

}
