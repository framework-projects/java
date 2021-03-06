/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.tools.jdeps;

import com.sun.tools.classfile.Dependency;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.module.ModuleDescriptor;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sun.tools.jdeps.Graph.*;
import static com.sun.tools.jdeps.JdepsFilter.DEFAULT_FILTER;
import static com.sun.tools.jdeps.Module.*;
import static java.lang.module.ModuleDescriptor.Requires.Modifier.*;
import static java.util.stream.Collectors.*;

/**
 * Analyze module dependences and compare with module descriptor.
 * Also identify any qualified exports not used by the target module.
 */
public class ModuleAnalyzer {
    private static final String JAVA_BASE = "java.base";

    private final JdepsConfiguration configuration;
    private final PrintWriter log;
    private final DependencyFinder dependencyFinder;
    private final Map<Module, ModuleDeps> modules;

    public ModuleAnalyzer(JdepsConfiguration config,
                          PrintWriter log,
                          Set<String> names) {
        this.configuration = config;
        this.log = log;

        this.dependencyFinder = new DependencyFinder(config, DEFAULT_FILTER);
        if (names.isEmpty()) {
            this.modules = configuration.rootModules().stream()
                .collect(toMap(Function.identity(), ModuleDeps::new));
        } else {
            this.modules = names.stream()
                .map(configuration::findModule)
                .flatMap(Optional::stream)
                .collect(toMap(Function.identity(), ModuleDeps::new));
        }
    }

    public boolean run() throws IOException {
        try {
            // compute "requires transitive" dependences
            modules.values().forEach(ModuleDeps::computeRequiresTransitive);

            modules.values().forEach(md -> {
                // compute "requires" dependences
                md.computeRequires();
                // apply transitive reduction and reports recommended requires.
                md.analyzeDeps();
            });
        } finally {
            dependencyFinder.shutdown();
        }
        return true;
    }

    class ModuleDeps {
        final Module root;
        Set<Module> requiresTransitive;
        Set<Module> requires;
        Map<String, Set<String>> unusedQualifiedExports;

        ModuleDeps(Module root) {
            this.root = root;
        }

        /**
         * Compute 'requires transitive' dependences by analyzing API dependencies
         */
        private void computeRequiresTransitive() {
            // record requires transitive
            this.requiresTransitive = computeRequires(true)
                .filter(m -> !m.name().equals(JAVA_BASE))
                .collect(toSet());

            trace("requires transitive: %s%n", requiresTransitive);
        }

        private void computeRequires() {
            this.requires = computeRequires(false).collect(toSet());
            trace("requires: %s%n", requires);
        }

        private Stream<Module> computeRequires(boolean apionly) {
            // analyze all classes

            if (apionly) {
                dependencyFinder.parseExportedAPIs(Stream.of(root));
            } else {
                dependencyFinder.parse(Stream.of(root));
            }

            // find the modules of all the dependencies found
            return dependencyFinder.getDependences(root)
                        .map(Archive::getModule);
        }

        ModuleDescriptor descriptor() {
            return descriptor(requiresTransitive, requires);
        }

        private ModuleDescriptor descriptor(Set<Module> requiresTransitive,
                                            Set<Module> requires) {

            ModuleDescriptor.Builder builder = ModuleDescriptor.newModule(root.name());

            if (!root.name().equals(JAVA_BASE))
                builder.requires(Set.of(MANDATED), JAVA_BASE);

            requiresTransitive.stream()
                .filter(m -> !m.name().equals(JAVA_BASE))
                .map(Module::name)
                .forEach(mn -> builder.requires(Set.of(TRANSITIVE), mn));

            requires.stream()
                .filter(m -> !requiresTransitive.contains(m))
                .filter(m -> !m.name().equals(JAVA_BASE))
                .map(Module::name)
                .forEach(mn -> builder.requires(mn));

            return builder.build();
        }

        private Graph<Module> buildReducedGraph() {
            ModuleGraphBuilder rpBuilder = new ModuleGraphBuilder(configuration);
            rpBuilder.addModule(root);
            requiresTransitive.stream()
                          .forEach(m -> rpBuilder.addEdge(root, m));

            // requires transitive graph
            Graph<Module> rbg = rpBuilder.build().reduce();

            ModuleGraphBuilder gb = new ModuleGraphBuilder(configuration);
            gb.addModule(root);
            requires.stream()
                    .forEach(m -> gb.addEdge(root, m));

            // transitive reduction
            Graph<Module> newGraph = gb.buildGraph().reduce(rbg);
            if (DEBUG) {
                System.err.println("after transitive reduction: ");
                newGraph.printGraph(log);
            }
            return newGraph;
        }

        /**
         * Apply the transitive reduction on the module graph
         * and returns the corresponding ModuleDescriptor
         */
        ModuleDescriptor reduced() {
            Graph<Module> g = buildReducedGraph();
            return descriptor(requiresTransitive, g.adjacentNodes(root));
        }

        /**
         * Apply transitive reduction on the resulting graph and reports
         * recommended requires.
         */
        private void analyzeDeps() {
            printModuleDescriptor(log, root);

            ModuleDescriptor analyzedDescriptor = descriptor();
            if (!matches(root.descriptor(), analyzedDescriptor)) {
                log.format("  [Suggested module descriptor for %s]%n", root.name());
                analyzedDescriptor.requires()
                    .stream()
                    .sorted(Comparator.comparing(ModuleDescriptor.Requires::name))
                    .forEach(req -> log.format("    requires %s;%n", req));
            }

            ModuleDescriptor reduced = reduced();
            if (!matches(root.descriptor(), reduced)) {
                log.format("  [Transitive reduced graph for %s]%n", root.name());
                reduced.requires()
                    .stream()
                    .sorted(Comparator.comparing(ModuleDescriptor.Requires::name))
                    .forEach(req -> log.format("    requires %s;%n", req));
            }

            checkQualifiedExports();
            log.println();
        }

        private void checkQualifiedExports() {
            // detect any qualified exports not used by the target module
            unusedQualifiedExports = unusedQualifiedExports();
            if (!unusedQualifiedExports.isEmpty())
                log.format("  [Unused qualified exports in %s]%n", root.name());

            unusedQualifiedExports.keySet().stream()
                .sorted()
                .forEach(pn -> log.format("    exports %s to %s%n", pn,
                    unusedQualifiedExports.get(pn).stream()
                        .sorted()
                        .collect(joining(","))));
        }

        private void printModuleDescriptor(PrintWriter out, Module module) {
            ModuleDescriptor descriptor = module.descriptor();
            out.format("%s (%s)%n", descriptor.name(), module.location());

            if (descriptor.name().equals(JAVA_BASE))
                return;

            out.println("  [Module descriptor]");
            descriptor.requires()
                .stream()
                .sorted(Comparator.comparing(ModuleDescriptor.Requires::name))
                .forEach(req -> out.format("    requires %s;%n", req));
        }


        /**
         * Detects any qualified exports not used by the target module.
         */
        private Map<String, Set<String>> unusedQualifiedExports() {
            Map<String, Set<String>> unused = new HashMap<>();

            // build the qualified exports map
            Map<String, Set<String>> qualifiedExports =
                root.exports().entrySet().stream()
                    .filter(e -> !e.getValue().isEmpty())
                    .map(Map.Entry::getKey)
                    .collect(toMap(Function.identity(), _k -> new HashSet<>()));

            Set<Module> mods = new HashSet<>();
            root.exports().values()
                .stream()
                .flatMap(Set::stream)
                .forEach(target -> configuration.findModule(target)
                    .ifPresentOrElse(mods::add,
                        () -> log.format("Warning: %s not found%n", target))
                );

            // parse all target modules
            dependencyFinder.parse(mods.stream());

            // adds to the qualified exports map if a module references it
            mods.stream().forEach(m ->
                m.getDependencies()
                    .map(Dependency.Location::getPackageName)
                    .filter(qualifiedExports::containsKey)
                    .forEach(pn -> qualifiedExports.get(pn).add(m.name())));

            // compare with the exports from ModuleDescriptor
            Set<String> staleQualifiedExports =
                qualifiedExports.keySet().stream()
                    .filter(pn -> !qualifiedExports.get(pn).equals(root.exports().get(pn)))
                    .collect(toSet());

            if (!staleQualifiedExports.isEmpty()) {
                for (String pn : staleQualifiedExports) {
                    Set<String> targets = new HashSet<>(root.exports().get(pn));
                    targets.removeAll(qualifiedExports.get(pn));
                    unused.put(pn, targets);
                }
            }
            return unused;
        }
    }

    private boolean matches(ModuleDescriptor md, ModuleDescriptor other) {
        // build requires transitive from ModuleDescriptor
        Set<ModuleDescriptor.Requires> reqTransitive = md.requires().stream()
            .filter(req -> req.modifiers().contains(TRANSITIVE))
            .collect(toSet());
        Set<ModuleDescriptor.Requires> otherReqTransitive = other.requires().stream()
            .filter(req -> req.modifiers().contains(TRANSITIVE))
            .collect(toSet());

        if (!reqTransitive.equals(otherReqTransitive)) {
            trace("mismatch requires transitive: %s%n", reqTransitive);
            return false;
        }

        Set<ModuleDescriptor.Requires> unused = md.requires().stream()
            .filter(req -> !other.requires().contains(req))
            .collect(Collectors.toSet());

        if (!unused.isEmpty()) {
            trace("mismatch requires: %s%n", unused);
            return false;
        }
        return true;
    }

    // ---- for testing purpose
    public ModuleDescriptor[] descriptors(String name) {
        ModuleDeps moduleDeps = modules.keySet().stream()
            .filter(m -> m.name().equals(name))
            .map(modules::get)
            .findFirst().get();

        ModuleDescriptor[] descriptors = new ModuleDescriptor[3];
        descriptors[0] = moduleDeps.root.descriptor();
        descriptors[1] = moduleDeps.descriptor();
        descriptors[2] = moduleDeps.reduced();
        return descriptors;
    }

    public Map<String, Set<String>> unusedQualifiedExports(String name) {
        ModuleDeps moduleDeps = modules.keySet().stream()
            .filter(m -> m.name().equals(name))
            .map(modules::get)
            .findFirst().get();
        return moduleDeps.unusedQualifiedExports;
    }
}
