/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.core.test;

import org.graalvm.compiler.lir.StandardOp.LoadConstantOp;
import org.graalvm.compiler.lir.StandardOp.MoveOp;
import org.graalvm.compiler.lir.StandardOp.ValueMoveOp;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.java.InstanceOfNode;
import org.graalvm.compiler.nodes.spi.CoreProviders;
import org.graalvm.compiler.phases.VerifyPhase;

import jdk.vm.ci.meta.MetaAccessProvider;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import jdk.vm.ci.meta.ResolvedJavaType;

/**
 * Checks that we do not use {@code instanceof} for types where faster alternatives are available.
 */
public class VerifyInstanceOfUsage extends VerifyPhase<CoreProviders> {

    private static final Class<?>[] FORBIDDEN_INSTANCE_OF_CHECKS = {
                    MoveOp.class,
                    ValueMoveOp.class,
                    LoadConstantOp.class
    };

    @Override
    public boolean checkContract() {
        return false;
    }

    @Override
    protected void verify(StructuredGraph graph, CoreProviders context) {
        final ResolvedJavaType[] bailoutType = new ResolvedJavaType[FORBIDDEN_INSTANCE_OF_CHECKS.length];
        for (int i = 0; i < FORBIDDEN_INSTANCE_OF_CHECKS.length; i++) {
            bailoutType[i] = context.getMetaAccess().lookupJavaType(FORBIDDEN_INSTANCE_OF_CHECKS[i]);
        }
        ResolvedJavaMethod method = graph.method();
        ResolvedJavaType declaringClass = method.getDeclaringClass();
        if (!isTrustedInterface(declaringClass, context.getMetaAccess())) {

            for (InstanceOfNode io : graph.getNodes().filter(InstanceOfNode.class)) {
                ResolvedJavaType type = io.type().getType();
                for (ResolvedJavaType forbiddenType : bailoutType) {
                    if (forbiddenType.equals(type)) {
                        String name = forbiddenType.getUnqualifiedName();
                        // strip outer class
                        ResolvedJavaType enclosingType = forbiddenType.getEnclosingType();
                        if (enclosingType != null) {
                            name = name.substring(enclosingType.getUnqualifiedName().length() + "$".length());
                        }
                        throw new VerificationError("Using `op instanceof %s` is not allowed. Use `%s.is%s(op)` instead. (in %s)", name, name, name, method.format("%H.%n(%p)"));
                    }
                }
            }
        }
    }

    private static boolean isTrustedInterface(ResolvedJavaType declaringClass, MetaAccessProvider metaAccess) {
        for (Class<?> trustedCls : FORBIDDEN_INSTANCE_OF_CHECKS) {
            ResolvedJavaType trusted = metaAccess.lookupJavaType(trustedCls);
            if (trusted.equals(declaringClass)) {
                return true;
            }
        }
        return false;
    }

}
