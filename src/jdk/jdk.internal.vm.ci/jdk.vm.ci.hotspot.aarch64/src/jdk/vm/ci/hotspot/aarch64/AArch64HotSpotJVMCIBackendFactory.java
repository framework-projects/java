/*
 * Copyright (c) 2015, 2019, Oracle and/or its affiliates. All rights reserved.
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
package jdk.vm.ci.hotspot.aarch64;

import jdk.vm.ci.aarch64.AArch64;
import jdk.vm.ci.code.Architecture;
import jdk.vm.ci.code.RegisterConfig;
import jdk.vm.ci.code.TargetDescription;
import jdk.vm.ci.code.stack.StackIntrospection;
import jdk.vm.ci.common.InitTimer;
import jdk.vm.ci.hotspot.*;
import jdk.vm.ci.meta.ConstantReflectionProvider;
import jdk.vm.ci.runtime.JVMCIBackend;

import java.util.EnumSet;

import static jdk.vm.ci.common.InitTimer.timer;

public class AArch64HotSpotJVMCIBackendFactory implements HotSpotJVMCIBackendFactory {

    private static EnumSet<AArch64.CPUFeature> computeFeatures(@SuppressWarnings("unused") AArch64HotSpotVMConfig config) {
        // Configure the feature set using the HotSpot flag settings.
        EnumSet<AArch64.CPUFeature> features = EnumSet.noneOf(AArch64.CPUFeature.class);

        if ((config.vmVersionFeatures & config.aarch64FP) != 0) {
            features.add(AArch64.CPUFeature.FP);
        }
        if ((config.vmVersionFeatures & config.aarch64ASIMD) != 0) {
            features.add(AArch64.CPUFeature.ASIMD);
        }
        if ((config.vmVersionFeatures & config.aarch64EVTSTRM) != 0) {
            features.add(AArch64.CPUFeature.EVTSTRM);
        }
        if ((config.vmVersionFeatures & config.aarch64AES) != 0) {
            features.add(AArch64.CPUFeature.AES);
        }
        if ((config.vmVersionFeatures & config.aarch64PMULL) != 0) {
            features.add(AArch64.CPUFeature.PMULL);
        }
        if ((config.vmVersionFeatures & config.aarch64SHA1) != 0) {
            features.add(AArch64.CPUFeature.SHA1);
        }
        if ((config.vmVersionFeatures & config.aarch64SHA2) != 0) {
            features.add(AArch64.CPUFeature.SHA2);
        }
        if ((config.vmVersionFeatures & config.aarch64CRC32) != 0) {
            features.add(AArch64.CPUFeature.CRC32);
        }
        if ((config.vmVersionFeatures & config.aarch64LSE) != 0) {
            features.add(AArch64.CPUFeature.LSE);
        }
        if ((config.vmVersionFeatures & config.aarch64STXR_PREFETCH) != 0) {
            features.add(AArch64.CPUFeature.STXR_PREFETCH);
        }
        if ((config.vmVersionFeatures & config.aarch64A53MAC) != 0) {
            features.add(AArch64.CPUFeature.A53MAC);
        }
        if ((config.vmVersionFeatures & config.aarch64DMB_ATOMICS) != 0) {
            features.add(AArch64.CPUFeature.DMB_ATOMICS);
        }

        return features;
    }

    private static EnumSet<AArch64.Flag> computeFlags(@SuppressWarnings("unused") AArch64HotSpotVMConfig config) {
        EnumSet<AArch64.Flag> flags = EnumSet.noneOf(AArch64.Flag.class);

        if (config.useBarriersForVolatile) {
            flags.add(AArch64.Flag.UseBarriersForVolatile);
        }
        if (config.useCRC32) {
            flags.add(AArch64.Flag.UseCRC32);
        }
        if (config.useNeon) {
            flags.add(AArch64.Flag.UseNeon);
        }
        if (config.useSIMDForMemoryOps) {
            flags.add(AArch64.Flag.UseSIMDForMemoryOps);
        }
        if (config.avoidUnalignedAccesses) {
            flags.add(AArch64.Flag.AvoidUnalignedAccesses);
        }
        if (config.useLSE) {
            flags.add(AArch64.Flag.UseLSE);
        }
        if (config.useBlockZeroing) {
            flags.add(AArch64.Flag.UseBlockZeroing);
        }

        return flags;
    }

    private static TargetDescription createTarget(AArch64HotSpotVMConfig config) {
        final int stackFrameAlignment = 16;
        final int implicitNullCheckLimit = 4096;
        final boolean inlineObjects = true;
        Architecture arch = new AArch64(computeFeatures(config), computeFlags(config));
        return new TargetDescription(arch, true, stackFrameAlignment, implicitNullCheckLimit, inlineObjects);
    }

    protected HotSpotConstantReflectionProvider createConstantReflection(HotSpotJVMCIRuntime runtime) {
        return new HotSpotConstantReflectionProvider(runtime);
    }

    private static RegisterConfig createRegisterConfig(TargetDescription target) {
        return new AArch64HotSpotRegisterConfig(target);
    }

    protected HotSpotCodeCacheProvider createCodeCache(HotSpotJVMCIRuntime runtime, TargetDescription target, RegisterConfig regConfig) {
        return new HotSpotCodeCacheProvider(runtime, target, regConfig);
    }

    protected HotSpotMetaAccessProvider createMetaAccess(HotSpotJVMCIRuntime runtime) {
        return new HotSpotMetaAccessProvider(runtime);
    }

    @Override
    public String getArchitecture() {
        return "aarch64";
    }

    @Override
    public String toString() {
        return "JVMCIBackend:" + getArchitecture();
    }

    @Override
    @SuppressWarnings("try")
    public JVMCIBackend createJVMCIBackend(HotSpotJVMCIRuntime runtime, JVMCIBackend host) {

        assert host == null;
        AArch64HotSpotVMConfig config = new AArch64HotSpotVMConfig(runtime.getConfigStore());
        TargetDescription target = createTarget(config);

        RegisterConfig regConfig;
        HotSpotCodeCacheProvider codeCache;
        ConstantReflectionProvider constantReflection;
        HotSpotMetaAccessProvider metaAccess;
        StackIntrospection stackIntrospection;
        try (InitTimer t = timer("create providers")) {
            try (InitTimer rt = timer("create MetaAccess provider")) {
                metaAccess = createMetaAccess(runtime);
            }
            try (InitTimer rt = timer("create RegisterConfig")) {
                regConfig = createRegisterConfig(target);
            }
            try (InitTimer rt = timer("create CodeCache provider")) {
                codeCache = createCodeCache(runtime, target, regConfig);
            }
            try (InitTimer rt = timer("create ConstantReflection provider")) {
                constantReflection = createConstantReflection(runtime);
            }
            try (InitTimer rt = timer("create StackIntrospection provider")) {
                stackIntrospection = new HotSpotStackIntrospection(runtime);
            }
        }
        try (InitTimer rt = timer("instantiate backend")) {
            return createBackend(metaAccess, codeCache, constantReflection, stackIntrospection);
        }
    }

    protected JVMCIBackend createBackend(HotSpotMetaAccessProvider metaAccess, HotSpotCodeCacheProvider codeCache, ConstantReflectionProvider constantReflection,
                    StackIntrospection stackIntrospection) {
        return new JVMCIBackend(metaAccess, codeCache, constantReflection, stackIntrospection);
    }
}
