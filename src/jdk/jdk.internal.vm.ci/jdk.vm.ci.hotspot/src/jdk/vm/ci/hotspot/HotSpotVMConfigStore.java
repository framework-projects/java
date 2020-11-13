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
package jdk.vm.ci.hotspot;

import jdk.vm.ci.common.InitTimer;
import jdk.vm.ci.common.JVMCIError;

import java.util.*;

import static jdk.vm.ci.common.InitTimer.timer;

/**
 * Access to VM configuration data.
 */
public final class HotSpotVMConfigStore {

    /**
     * Gets the C++ symbols whose addresses are exposed by this object.
     *
     * @return an unmodifiable map from the symbol names to their addresses
     */
    public Map<String, Long> getAddresses() {
        return Collections.unmodifiableMap(vmAddresses);
    }

    /**
     * Gets the C++ constants exposed by this object.
     *
     * @return an unmodifiable map from the names of C++ constants to their values
     */
    public Map<String, Long> getConstants() {
        return Collections.unmodifiableMap(vmConstants);
    }

    /**
     * Gets the VM flags exposed by this object.
     *
     * @return an unmodifiable map from VM flag names to {@link VMFlag} objects
     */
    public Map<String, VMFlag> getFlags() {
        return Collections.unmodifiableMap(vmFlags);
    }

    /**
     * Gets the C++ fields exposed by this object.
     *
     * @return an unmodifiable map from VM field names to {@link VMField} objects
     */
    public Map<String, VMField> getFields() {
        return Collections.unmodifiableMap(vmFields);
    }

    /**
     * Gets the VM intrinsic descriptions exposed by this object.
     */
    public List<VMIntrinsicMethod> getIntrinsics() {
        return Collections.unmodifiableList(vmIntrinsics);
    }

    final HashMap<String, VMField> vmFields;
    final HashMap<String, Long> vmConstants;
    final HashMap<String, Long> vmAddresses;
    final HashMap<String, VMFlag> vmFlags;
    final List<VMIntrinsicMethod> vmIntrinsics;
    final CompilerToVM compilerToVm;

    /**
     * Reads the database of VM info. The return value encodes the info in a nested object array
     * that is described by the pseudo Java object {@code info} below:
     *
     * <pre>
     *     info = [
     *         VMField[] vmFields,
     *         [String name, Long value, ...] vmConstants,
     *         [String name, Long value, ...] vmAddresses,
     *         VMFlag[] vmFlags
     *         VMIntrinsicMethod[] vmIntrinsics
     *     ]
     * </pre>
     */
    @SuppressWarnings("try")
    HotSpotVMConfigStore(CompilerToVM compilerToVm) {
        this.compilerToVm = compilerToVm;
        Object[] data;
        try (InitTimer t = timer("CompilerToVm readConfiguration")) {
            data = compilerToVm.readConfiguration();
        }
        if (data.length != 5) {
            throw new JVMCIError("Expected data.length to be 5, not %d", data.length);
        }

        // @formatter:off
        VMField[] vmFieldsInfo    = (VMField[]) data[0];
        Object[] vmConstantsInfo  = (Object[])  data[1];
        Object[] vmAddressesInfo  = (Object[])  data[2];
        VMFlag[] vmFlagsInfo      = (VMFlag[])  data[3];

        vmFields     = new HashMap<>(vmFieldsInfo.length);
        vmConstants  = new HashMap<>(vmConstantsInfo.length);
        vmAddresses  = new HashMap<>(vmAddressesInfo.length);
        vmFlags      = new HashMap<>(vmFlagsInfo.length);
        vmIntrinsics = Arrays.asList((VMIntrinsicMethod[]) data[4]);
        // @formatter:on

        try (InitTimer t = timer("HotSpotVMConfigStore<init> fill maps")) {
            for (VMField vmField : vmFieldsInfo) {
                vmFields.put(vmField.name, vmField);
            }

            for (int i = 0; i < vmConstantsInfo.length / 2; i++) {
                String name = (String) vmConstantsInfo[i * 2];
                Long value = (Long) vmConstantsInfo[i * 2 + 1];
                vmConstants.put(name, value);
            }

            for (int i = 0; i < vmAddressesInfo.length / 2; i++) {
                String name = (String) vmAddressesInfo[i * 2];
                Long value = (Long) vmAddressesInfo[i * 2 + 1];
                vmAddresses.put(name, value);
            }

            for (VMFlag vmFlag : vmFlagsInfo) {
                vmFlags.put(vmFlag.name, vmFlag);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%d fields, %d constants, %d addresses, %d flags, %d intrinsics]",
                        getClass().getSimpleName(),
                        vmFields.size(),
                        vmConstants.size(),
                        vmAddresses.size(),
                        vmFlags.size(),
                        vmIntrinsics.size());
    }

    void printConfig() {
        CompilerToVM vm = compilerToVm;
        TreeMap<String, VMField> fields = new TreeMap<>(getFields());
        for (VMField field : fields.values()) {
            if (!field.isStatic()) {
                printConfigLine(vm, "[vmconfig:instance field] %s %s {offset=%d[0x%x]}%n", field.type, field.name, field.offset, field.offset);
            } else {
                String value = field.value == null ? "null" : field.value instanceof Boolean ? field.value.toString() : String.format("%d[0x%x]", field.value, field.value);
                printConfigLine(vm, "[vmconfig:static field] %s %s = %s {address=0x%x}%n", field.type, field.name, value, field.address);
            }
        }
        TreeMap<String, VMFlag> flags = new TreeMap<>(getFlags());
        for (VMFlag flag : flags.values()) {
            printConfigLine(vm, "[vmconfig:flag] %s %s = %s%n", flag.type, flag.name, flag.value);
        }
        TreeMap<String, Long> addresses = new TreeMap<>(getAddresses());
        for (Map.Entry<String, Long> e : addresses.entrySet()) {
            printConfigLine(vm, "[vmconfig:address] %s = %d[0x%x]%n", e.getKey(), e.getValue(), e.getValue());
        }
        TreeMap<String, Long> constants = new TreeMap<>(getConstants());
        for (Map.Entry<String, Long> e : constants.entrySet()) {
            printConfigLine(vm, "[vmconfig:constant] %s = %d[0x%x]%n", e.getKey(), e.getValue(), e.getValue());
        }
        for (VMIntrinsicMethod e : getIntrinsics()) {
            printConfigLine(vm, "[vmconfig:intrinsic] %d = %s.%s %s%n", e.id, e.declaringClass, e.name, e.descriptor);
        }
    }

    @SuppressFBWarnings(value = "DM_DEFAULT_ENCODING", justification = "no localization here please!")
    private static void printConfigLine(CompilerToVM vm, String format, Object... args) {
        String line = String.format(format, args);
        byte[] lineBytes = line.getBytes();
        vm.writeDebugOutput(lineBytes, 0, lineBytes.length, true, true);
    }

}