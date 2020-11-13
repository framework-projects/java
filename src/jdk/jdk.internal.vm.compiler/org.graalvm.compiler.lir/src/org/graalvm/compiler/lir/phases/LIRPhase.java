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


package org.graalvm.compiler.lir.phases;

import jdk.vm.ci.code.TargetDescription;
import org.graalvm.compiler.debug.DebugCloseable;
import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.debug.MemUseTrackerKey;
import org.graalvm.compiler.debug.TimerKey;
import org.graalvm.compiler.lir.LIR;
import org.graalvm.compiler.lir.gen.LIRGenerationResult;
import org.graalvm.compiler.options.Option;
import org.graalvm.compiler.options.OptionKey;
import org.graalvm.compiler.options.OptionType;

import java.util.regex.Pattern;

/**
 * Base class for all {@link LIR low-level} phases. Subclasses should be stateless. There will be
 * one global instance for each phase that is shared for all compilations.
 */
public abstract class LIRPhase<C> {

    public static class Options {
        // @formatter:off
        @Option(help = "Enable LIR level optimiztations.", type = OptionType.Debug)
        public static final OptionKey<Boolean> LIROptimization = new OptionKey<>(true);
        // @formatter:on
    }

    /**
     * Records time spent within {@link #apply}.
     */
    private final TimerKey timer;

    /**
     * Records memory usage within {@link #apply}.
     */
    private final MemUseTrackerKey memUseTracker;

    public static final class LIRPhaseStatistics {
        /**
         * Records time spent within {@link #apply}.
         */
        public final TimerKey timer;

        /**
         * Records memory usage within {@link #apply}.
         */
        public final MemUseTrackerKey memUseTracker;

        public LIRPhaseStatistics(Class<?> clazz) {
            timer = DebugContext.timer("LIRPhaseTime_%s", clazz);
            memUseTracker = DebugContext.memUseTracker("LIRPhaseMemUse_%s", clazz);
        }
    }

    public static final ClassValue<LIRPhaseStatistics> statisticsClassValue = new ClassValue<LIRPhaseStatistics>() {
        @Override
        protected LIRPhaseStatistics computeValue(Class<?> c) {
            return new LIRPhaseStatistics(c);
        }
    };

    public static LIRPhaseStatistics getLIRPhaseStatistics(Class<?> c) {
        return statisticsClassValue.get(c);
    }

    /** Lazy initialization to create pattern only when assertions are enabled. */
    static class NamePatternHolder {
        static final Pattern NAME_PATTERN = Pattern.compile("[A-Z][A-Za-z0-9]+");
    }

    private static boolean checkName(CharSequence name) {
        assert name == null || NamePatternHolder.NAME_PATTERN.matcher(name).matches() : "illegal phase name: " + name;
        return true;
    }

    public LIRPhase() {
        LIRPhaseStatistics statistics = getLIRPhaseStatistics(getClass());
        timer = statistics.timer;
        memUseTracker = statistics.memUseTracker;
    }

    public final void apply(TargetDescription target, LIRGenerationResult lirGenRes, C context) {
        apply(target, lirGenRes, context, true);
    }

    @SuppressWarnings("try")
    public final void apply(TargetDescription target, LIRGenerationResult lirGenRes, C context, boolean dumpLIR) {
        DebugContext debug = lirGenRes.getLIR().getDebug();
        try (DebugContext.Scope s = debug.scope(getName(), this)) {
            try (DebugCloseable a = timer.start(debug); DebugCloseable c = memUseTracker.start(debug)) {
                run(target, lirGenRes, context);
                if (dumpLIR && debug.areScopesEnabled()) {
                    dumpAfter(lirGenRes);
                }
            }
        } catch (Throwable e) {
            throw debug.handle(e);
        }
    }

    private void dumpAfter(LIRGenerationResult lirGenRes) {
        boolean isStage = this instanceof LIRPhaseSuite;
        if (!isStage) {
            DebugContext debug = lirGenRes.getLIR().getDebug();
            if (debug.isDumpEnabled(DebugContext.INFO_LEVEL)) {
                debug.dump(DebugContext.INFO_LEVEL, lirGenRes.getLIR(), "After %s", getName());
            }
        }
    }

    protected abstract void run(TargetDescription target, LIRGenerationResult lirGenRes, C context);

    public static CharSequence createName(Class<?> clazz) {
        String className = clazz.getName();
        String s = className.substring(className.lastIndexOf(".") + 1); // strip the package name
        int innerClassPos = s.indexOf('$');
        if (innerClassPos > 0) {
            /* Remove inner class name. */
            s = s.substring(0, innerClassPos);
        }
        if (s.endsWith("Phase")) {
            s = s.substring(0, s.length() - "Phase".length());
        }
        return s;
    }

    protected CharSequence createName() {
        return createName(getClass());
    }

    public final CharSequence getName() {
        CharSequence name = createName();
        assert checkName(name);
        return name;
    }
}