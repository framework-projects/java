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


package org.graalvm.compiler.core.common.alloc;

import org.graalvm.compiler.core.common.cfg.AbstractBlockBase;
import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.debug.Indent;

import java.util.List;

public final class TraceStatisticsPrinter {
    private static final String SEP = ";";

    @SuppressWarnings("try")
    public static void printTraceStatistics(DebugContext debug, TraceBuilderResult result, String compilationUnitName) {
        try (DebugContext.Scope s = debug.scope("DumpTraceStatistics")) {
            if (debug.isLogEnabled(DebugContext.VERBOSE_LEVEL)) {
                print(debug, result, compilationUnitName);
            }
        } catch (Throwable e) {
            debug.handle(e);
        }
    }

    @SuppressWarnings("try")
    protected static void print(DebugContext debug, TraceBuilderResult result, String compilationUnitName) {
        List<Trace> traces = result.getTraces();
        int numTraces = traces.size();

        try (Indent indent0 = debug.logAndIndent(DebugContext.VERBOSE_LEVEL, "<tracestatistics>")) {
            debug.log(DebugContext.VERBOSE_LEVEL, "<name>%s</name>", compilationUnitName != null ? compilationUnitName : "null");
            try (Indent indent1 = debug.logAndIndent(DebugContext.VERBOSE_LEVEL, "<traces>")) {
                printRawLine(debug, "tracenumber", "total", "min", "max", "numBlocks");
                for (int i = 0; i < numTraces; i++) {
                    AbstractBlockBase<?>[] t = traces.get(i).getBlocks();
                    double total = 0;
                    double max = Double.NEGATIVE_INFINITY;
                    double min = Double.POSITIVE_INFINITY;
                    for (AbstractBlockBase<?> block : t) {
                        double frequency = block.getRelativeFrequency();
                        total += frequency;
                        if (frequency < min) {
                            min = frequency;
                        }
                        if (frequency > max) {
                            max = frequency;
                        }
                    }
                    printLine(debug, i, total, min, max, t.length);
                }
            }
            debug.log(DebugContext.VERBOSE_LEVEL, "</traces>");
        }
        debug.log(DebugContext.VERBOSE_LEVEL, "</tracestatistics>");

    }

    private static void printRawLine(DebugContext debug, Object tracenr, Object totalTime, Object minProb, Object maxProb, Object numBlocks) {
        debug.log(DebugContext.VERBOSE_LEVEL, "%s", String.join(SEP, tracenr.toString(), totalTime.toString(), minProb.toString(), maxProb.toString(), numBlocks.toString()));
    }

    private static void printLine(DebugContext debug, int tracenr, double totalTime, double minProb, double maxProb, int numBlocks) {
        printRawLine(debug, tracenr, totalTime, minProb, maxProb, numBlocks);
    }
}
