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


package org.graalvm.compiler.core.test;

import org.graalvm.compiler.core.common.PermanentBailoutException;
import org.graalvm.compiler.core.common.RetryableBailoutException;
import org.graalvm.compiler.debug.GraalError;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.java.MethodCallTargetNode;
import org.graalvm.compiler.nodes.spi.CoreProviders;
import org.graalvm.compiler.phases.VerifyPhase;

import jdk.vm.ci.code.BailoutException;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import jdk.vm.ci.meta.ResolvedJavaType;

public class VerifyBailoutUsage extends VerifyPhase<CoreProviders> {

    private static final String[] AllowedPackagePrefixes;

    private static String getPackageName(Class<?> c) {
        String classNameWithPackage = c.getName();
        String simpleName = c.getSimpleName();
        return classNameWithPackage.substring(0, classNameWithPackage.length() - simpleName.length() - 1);
    }

    static {
        try {
            AllowedPackagePrefixes = new String[]{
                            getPackageName(PermanentBailoutException.class),
                            "jdk.vm.ci",

                            // Allows GraalTruffleRuntime.handleAnnotationFailure to throw
                            // a BailoutException since the org.graalvm.compiler.truffle.runtime
                            // project can not see the PermanentBailoutException or
                            // RetryableBailoutException types.
                            "org.graalvm.compiler.truffle.runtime"
            };
        } catch (Throwable t) {
            throw new GraalError(t);
        }
    }

    private static boolean matchesPrefix(String packageName) {
        for (String allowedPackagePrefix : AllowedPackagePrefixes) {
            if (packageName.startsWith(allowedPackagePrefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void verify(StructuredGraph graph, CoreProviders context) {
        final ResolvedJavaType bailoutType = context.getMetaAccess().lookupJavaType(BailoutException.class);
        ResolvedJavaMethod caller = graph.method();
        String holderQualified = caller.format("%H");
        String holderUnqualified = caller.format("%h");
        String packageName = holderQualified.substring(0, holderQualified.length() - holderUnqualified.length() - 1);
        if (!matchesPrefix(packageName)) {
            for (MethodCallTargetNode t : graph.getNodes(MethodCallTargetNode.TYPE)) {
                ResolvedJavaMethod callee = t.targetMethod();
                if (callee.getDeclaringClass().equals(bailoutType)) {
                    // we only allow the getter
                    if (!callee.getName().equals("isPermanent")) {
                        throw new VerificationError("Call to %s at callsite %s is prohibited. Consider using %s for permanent bailouts or %s for retryables.", callee.format("%H.%n(%p)"),
                                        caller.format("%H.%n(%p)"), PermanentBailoutException.class.getName(),
                                        RetryableBailoutException.class.getName());
                    }
                }
            }
        }
    }

}
