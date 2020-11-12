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


package jdk.tools.jaotc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.graalvm.compiler.hotspot.meta.HotSpotInvokeDynamicPlugin.DynamicTypeStore;

import jdk.vm.ci.hotspot.HotSpotConstantPool;
import jdk.vm.ci.hotspot.HotSpotConstantPoolObject;
import jdk.vm.ci.hotspot.HotSpotObjectConstant;
import jdk.vm.ci.hotspot.HotSpotResolvedJavaMethod;
import jdk.vm.ci.hotspot.HotSpotResolvedObjectType;
import jdk.vm.ci.meta.JavaConstant;

final class AOTDynamicTypeStore implements DynamicTypeStore {

    public static class Location {
        private HotSpotResolvedObjectType holder;
        private int cpi;

        Location(HotSpotResolvedObjectType holder, int cpi) {
            this.holder = holder;
            this.cpi = cpi;
        }

        public HotSpotResolvedObjectType getHolder() {
            return holder;
        }

        public int getCpi() {
            return cpi;
        }

        @Override
        public String toString() {
            return getHolder().getName() + "@" + cpi;
        }

        @Override
        public int hashCode() {
            return holder.hashCode() + getClass().hashCode() + cpi;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (getClass() != o.getClass()) {
                return false;
            }
            Location l = (Location) o;
            return cpi == l.cpi && holder.equals(l.holder);
        }
    }

    public static class AdapterLocation extends Location {
        private int methodId;

        AdapterLocation(HotSpotResolvedObjectType holder, int cpi, int methodId) {
            super(holder, cpi);
            this.methodId = methodId;
        }

        public int getMethodId() {
            return methodId;
        }

        @Override
        public String toString() {
            return "adapter:" + methodId + "@" + super.toString();
        }
    }

    public static class AppendixLocation extends Location {
        AppendixLocation(HotSpotResolvedObjectType holder, int cpi) {
            super(holder, cpi);
        }

        @Override
        public String toString() {
            return "appendix@" + super.toString();
        }
    }

    private HashMap<HotSpotResolvedObjectType, HashSet<Location>> typeMap = new HashMap<>();
    private HashMap<HotSpotResolvedObjectType, HashSet<HotSpotResolvedObjectType>> holderMap = new HashMap<>();

    public Set<HotSpotResolvedObjectType> getDynamicTypes() {
        synchronized (typeMap) {
            return typeMap.keySet();
        }
    }

    public Set<HotSpotResolvedObjectType> getDynamicHolders() {
        synchronized (holderMap) {
            return holderMap.keySet();
        }
    }

    @Override
    public void recordAdapter(int opcode, HotSpotResolvedObjectType holder, int index, HotSpotResolvedJavaMethod adapter) {
        int cpi = ((HotSpotConstantPool) holder.getConstantPool()).rawIndexToConstantPoolIndex(index, opcode);
        int methodId = adapter.methodIdnum();
        HotSpotResolvedObjectType adapterType = adapter.getDeclaringClass();
        recordDynamicTypeLocation(new AdapterLocation(holder, cpi, methodId), adapterType);
    }

    @Override
    public JavaConstant recordAppendix(int opcode, HotSpotResolvedObjectType holder, int index, JavaConstant appendix) {
        int cpi = ((HotSpotConstantPool) holder.getConstantPool()).rawIndexToConstantPoolIndex(index, opcode);
        HotSpotResolvedObjectType appendixType = ((HotSpotObjectConstant) appendix).getType();
        recordDynamicTypeLocation(new AppendixLocation(holder, cpi), appendixType);
        // Make the constant locatable
        return HotSpotConstantPoolObject.forObject(holder, cpi, appendix);
    }

    private static <T> void recordDynamicMapValue(HashMap<HotSpotResolvedObjectType, HashSet<T>> map, HotSpotResolvedObjectType type, T v) {
        synchronized (map) {
            HashSet<T> set = map.get(type);
            if (set == null) {
                set = new HashSet<>();
                map.put(type, set);
            }
            set.add(v);
        }
    }

    private void recordDynamicTypeLocation(Location l, HotSpotResolvedObjectType type) {
        recordDynamicMapValue(typeMap, type, l);
        HotSpotResolvedObjectType holder = l.getHolder();
        recordDynamicMapValue(holderMap, holder, type);
    }

    public Set<Location> getDynamicClassLocationsForType(HotSpotResolvedObjectType type) {
        synchronized (typeMap) {
            return typeMap.get(type);
        }
    }

    public Set<HotSpotResolvedObjectType> getDynamicTypesForHolder(HotSpotResolvedObjectType holder) {
        synchronized (holderMap) {
            return holderMap.get(holder);
        }
    }

}
