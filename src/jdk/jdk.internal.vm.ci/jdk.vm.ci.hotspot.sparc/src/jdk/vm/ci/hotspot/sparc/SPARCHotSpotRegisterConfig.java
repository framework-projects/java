/*
 * Copyright (c) 2013, 2016, Oracle and/or its affiliates. All rights reserved.
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
package jdk.vm.ci.hotspot.sparc;

import jdk.vm.ci.code.*;
import jdk.vm.ci.code.CallingConvention.Type;
import jdk.vm.ci.common.JVMCIError;
import jdk.vm.ci.hotspot.HotSpotCallingConventionType;
import jdk.vm.ci.meta.*;
import jdk.vm.ci.sparc.SPARC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static jdk.vm.ci.meta.JavaKind.Void;
import static jdk.vm.ci.meta.Value.ILLEGAL;
import static jdk.vm.ci.sparc.SPARC.*;

public class SPARCHotSpotRegisterConfig implements RegisterConfig {

    private final TargetDescription target;

    private final RegisterArray allocatable;

    private final RegisterAttributes[] attributesMap;

    @Override
    public RegisterArray getAllocatableRegisters() {
        return allocatable;
    }

    @Override
    public RegisterArray filterAllocatableRegisters(PlatformKind kind, RegisterArray registers) {
        ArrayList<Register> list = new ArrayList<>();
        for (Register reg : registers) {
            if (target.arch.canStoreValue(reg.getRegisterCategory(), kind)) {
                list.add(reg);
            }
        }
        return new RegisterArray(list);
    }

    @Override
    public RegisterAttributes[] getAttributesMap() {
        return attributesMap.clone();
    }

    private final RegisterArray cpuCallerParameterRegisters = new RegisterArray(o0, o1, o2, o3, o4, o5);
    private final RegisterArray cpuCalleeParameterRegisters = new RegisterArray(i0, i1, i2, i3, i4, i5);

    private final RegisterArray fpuFloatJavaParameterRegisters = new RegisterArray(f0, f1, f2, f3, f4, f5, f6, f7);
    private final RegisterArray fpuDoubleJavaParameterRegisters = new RegisterArray(d0, null, d2, null, d4, null, d6, null);

    // @formatter:off
    private final RegisterArray fpuFloatNativeParameterRegisters = new RegisterArray(
                    f1,   f3,  f5,  f7,  f9, f11, f13, f15,
                    f17, f19, f21, f23, f25, f27, f29, f31);

    private final RegisterArray fpuDoubleNativeParameterRegisters = new RegisterArray(
                     d0,  d2,  d4,  d6,  d8, d10, d12, d14,
                    d16, d18, d20, d22, d24, d26, d28, d30);

    private final RegisterArray callerSaveRegisters;

    /**
     * This lists all L and I registers which are saved in the register window.
     */
    private final RegisterArray windowSaveRegisters = new RegisterArray(
                    l0, l1, l2, l3, l4, l5, l6, l7,
                    i0, i1, i2, i3, i4, i5, i6, i7);
    // @formatter:on

    private static final RegisterArray reservedRegisters = new RegisterArray(sp, g0, g2);

    private static RegisterArray initAllocatable(Architecture arch, boolean reserveForHeapBase) {
        RegisterArray allRegisters = arch.getAvailableValueRegisters();
        Register[] registers = new Register[allRegisters.size() - reservedRegisters.size() - (reserveForHeapBase ? 1 : 0)];
        List<Register> reservedRegistersList = reservedRegisters.asList();

        int idx = 0;
        for (Register reg : allRegisters) {
            if (reservedRegistersList.contains(reg)) {
                // skip reserved registers
                continue;
            }
            if (reserveForHeapBase && reg.equals(g6)) {
                // skip heap base register
                continue;
            }

            registers[idx++] = reg;
        }

        assert idx == registers.length;
        return new RegisterArray(registers);
    }

    public SPARCHotSpotRegisterConfig(TargetDescription target, boolean useCompressedOops) {
        this(target, initAllocatable(target.arch, useCompressedOops));
    }

    public SPARCHotSpotRegisterConfig(TargetDescription target, RegisterArray allocatable) {
        this.target = target;
        this.allocatable = allocatable;
        HashSet<Register> callerSaveSet = new HashSet<>(target.arch.getAvailableValueRegisters().asList());
        for (Register cs : windowSaveRegisters) {
            callerSaveSet.remove(cs);
        }
        this.callerSaveRegisters = new RegisterArray(callerSaveSet);
        attributesMap = RegisterAttributes.createMap(this, SPARC.allRegisters);
    }

    @Override
    public RegisterArray getCallerSaveRegisters() {
        return callerSaveRegisters;
    }

    @Override
    public RegisterArray getCalleeSaveRegisters() {
        return null;
    }

    @Override
    public boolean areAllAllocatableRegistersCallerSaved() {
        return false;
    }

    @Override
    public CallingConvention getCallingConvention(Type type, JavaType returnType, JavaType[] parameterTypes, ValueKindFactory<?> valueKindFactory) {
        HotSpotCallingConventionType hotspotType = (HotSpotCallingConventionType) type;
        if (type == HotSpotCallingConventionType.JavaCall || type == HotSpotCallingConventionType.NativeCall) {
            return callingConvention(cpuCallerParameterRegisters, returnType, parameterTypes, hotspotType, valueKindFactory);
        }
        if (type == HotSpotCallingConventionType.JavaCallee) {
            return callingConvention(cpuCalleeParameterRegisters, returnType, parameterTypes, hotspotType, valueKindFactory);
        }
        throw JVMCIError.shouldNotReachHere();
    }

    @Override
    public RegisterArray getCallingConventionRegisters(Type type, JavaKind kind) {
        HotSpotCallingConventionType hotspotType = (HotSpotCallingConventionType) type;
        switch (kind) {
            case Boolean:
            case Byte:
            case Short:
            case Char:
            case Int:
            case Long:
            case Object:
                return hotspotType == HotSpotCallingConventionType.JavaCallee ? cpuCalleeParameterRegisters : cpuCallerParameterRegisters;
            case Double:
            case Float:
                return fpuFloatJavaParameterRegisters;
            default:
                throw JVMCIError.shouldNotReachHere("Unknown JavaKind " + kind);
        }
    }

    private CallingConvention callingConvention(RegisterArray generalParameterRegisters, JavaType returnType, JavaType[] parameterTypes, HotSpotCallingConventionType type,
                    ValueKindFactory<?> valueKindFactory) {
        AllocatableValue[] locations = new AllocatableValue[parameterTypes.length];

        int currentGeneral = 0;
        int currentFloating = 0;
        int currentStackOffset = 0;
        boolean isNative = type == HotSpotCallingConventionType.NativeCall;

        for (int i = 0; i < parameterTypes.length; i++) {
            final JavaKind kind = parameterTypes[i].getJavaKind().getStackKind();
            if (isNative) {
                RegisterArray registerSet;
                switch (kind) {
                    case Byte:
                    case Boolean:
                    case Short:
                    case Char:
                    case Int:
                    case Long:
                    case Object:
                        registerSet = generalParameterRegisters;
                        break;
                    case Double:
                        registerSet = fpuDoubleNativeParameterRegisters;
                        break;
                    case Float:
                        registerSet = fpuFloatNativeParameterRegisters;
                        break;
                    default:
                        throw JVMCIError.shouldNotReachHere();
                }
                if (i < registerSet.size()) {
                    locations[i] = registerSet.get(i).asValue(valueKindFactory.getValueKind(kind));
                    currentStackOffset += target.arch.getWordSize();
                }
            } else {
                switch (kind) {
                    case Byte:
                    case Boolean:
                    case Short:
                    case Char:
                    case Int:
                    case Long:
                    case Object:
                        if (currentGeneral < generalParameterRegisters.size()) {
                            Register register = generalParameterRegisters.get(currentGeneral++);
                            locations[i] = register.asValue(valueKindFactory.getValueKind(kind));
                        }
                        break;
                    case Double:
                        if (currentFloating < fpuFloatJavaParameterRegisters.size()) {
                            if (currentFloating % 2 != 0) {
                                // Make register number even to be a double reg
                                currentFloating++;
                            }
                            Register register = fpuDoubleJavaParameterRegisters.get(currentFloating);
                            currentFloating += 2; // Only every second is a double register
                            locations[i] = register.asValue(valueKindFactory.getValueKind(kind));
                        }
                        break;
                    case Float:
                        if (currentFloating < fpuFloatJavaParameterRegisters.size()) {
                            Register register = fpuFloatJavaParameterRegisters.get(currentFloating++);
                            locations[i] = register.asValue(valueKindFactory.getValueKind(kind));
                        }
                        break;
                    default:
                        throw JVMCIError.shouldNotReachHere();
                }
            }

            if (locations[i] == null) {
                ValueKind<?> valueKind = valueKindFactory.getValueKind(kind);
                int typeSize = valueKind.getPlatformKind().getSizeInBytes();
                if (isNative) {
                    currentStackOffset += target.arch.getWordSize() - typeSize;
                }
                currentStackOffset = roundUp(currentStackOffset, typeSize);
                int slotOffset = currentStackOffset + REGISTER_SAFE_AREA_SIZE;
                locations[i] = StackSlot.get(valueKind, slotOffset, !type.out);
                currentStackOffset += typeSize;
            }
        }

        JavaKind returnKind = returnType == null ? Void : returnType.getJavaKind();
        AllocatableValue returnLocation = returnKind == Void ? ILLEGAL : getReturnRegister(returnKind, type).asValue(valueKindFactory.getValueKind(returnKind.getStackKind()));
        return new CallingConvention(currentStackOffset, returnLocation, locations);
    }

    private static int roundUp(int number, int mod) {
        return ((number + mod - 1) / mod) * mod;
    }

    @Override
    public Register getReturnRegister(JavaKind kind) {
        return getReturnRegister(kind, HotSpotCallingConventionType.JavaCallee);
    }

    private static Register getReturnRegister(JavaKind kind, HotSpotCallingConventionType type) {
        switch (kind) {
            case Boolean:
            case Byte:
            case Char:
            case Short:
            case Int:
            case Long:
            case Object:
                return type == HotSpotCallingConventionType.JavaCallee ? i0 : o0;
            case Float:
                return f0;
            case Double:
                return d0;
            case Void:
            case Illegal:
                return null;
            default:
                throw new UnsupportedOperationException("no return register for type " + kind);
        }
    }

    @Override
    public Register getFrameRegister() {
        return sp;
    }

    @Override
    public String toString() {
        return String.format("Allocatable: " + getAllocatableRegisters() + "%n" + "CallerSave:  " + getCallerSaveRegisters() + "%n");
    }
}
