/*
 * Copyright (c) 2003, 2015, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.management.internal;

import jdk.internal.platform.Metrics;
import sun.management.BaseOperatingSystemImpl;
import sun.management.VMManagement;

import java.util.concurrent.TimeUnit;
/**
 * Implementation class for the operating system.
 * Standard and committed hotspot-specific metrics if any.
 *
 * ManagementFactory.getOperatingSystemMXBean() returns an instance
 * of this class.
 */
class OperatingSystemImpl extends BaseOperatingSystemImpl
    implements com.sun.management.UnixOperatingSystemMXBean {

    private static final int MAX_ATTEMPTS_NUMBER = 10;
    private final Metrics containerMetrics;

    OperatingSystemImpl(VMManagement vm) {
        super(vm);
        this.containerMetrics = jdk.internal.platform.Container.metrics();
    }

    public long getCommittedVirtualMemorySize() {
        return getCommittedVirtualMemorySize0();
    }

    public long getTotalSwapSpaceSize() {
        if (containerMetrics != null) {
            long limit = containerMetrics.getMemoryAndSwapLimit();
            // The memory limit metrics is not available if JVM runs on Linux host (not in a docker container)
            // or if a docker container was started without specifying a memory limit (without '--memory='
            // Docker option). In latter case there is no limit on how much memory the container can use and
            // it can use as much memory as the host's OS allows.
            long memLimit = containerMetrics.getMemoryLimit();
            if (limit >= 0 && memLimit >= 0) {
                return limit - memLimit;
            }
        }
        return getTotalSwapSpaceSize0();
    }

    public long getFreeSwapSpaceSize() {
        if (containerMetrics != null) {
            long memSwapLimit = containerMetrics.getMemoryAndSwapLimit();
            long memLimit = containerMetrics.getMemoryLimit();
            if (memSwapLimit >= 0 && memLimit >= 0) {
                for (int attempt = 0; attempt < MAX_ATTEMPTS_NUMBER; attempt++) {
                    long memSwapUsage = containerMetrics.getMemoryAndSwapUsage();
                    long memUsage = containerMetrics.getMemoryUsage();
                    if (memSwapUsage > 0 && memUsage > 0) {
                        // We read "memory usage" and "memory and swap usage" not atomically,
                        // and it's possible to get the negative value when subtracting these two.
                        // If this happens just retry the loop for a few iterations.
                        if ((memSwapUsage - memUsage) >= 0) {
                            return memSwapLimit - memLimit - (memSwapUsage - memUsage);
                        }
                    }
                }
            }
        }
        return getFreeSwapSpaceSize0();
    }

    public long getProcessCpuTime() {
        return getProcessCpuTime0();
    }

    public long getFreePhysicalMemorySize() {
        if (containerMetrics != null) {
            long usage = containerMetrics.getMemoryUsage();
            long limit = containerMetrics.getMemoryLimit();
            if (usage > 0 && limit >= 0) {
                return limit - usage;
            }
        }
        return getFreePhysicalMemorySize0();
    }

    public long getTotalPhysicalMemorySize() {
        if (containerMetrics != null) {
            long limit = containerMetrics.getMemoryLimit();
            if (limit >= 0) {
                return limit;
            }
        }
        return getTotalPhysicalMemorySize0();
    }

    public long getOpenFileDescriptorCount() {
        return getOpenFileDescriptorCount0();
    }

    public long getMaxFileDescriptorCount() {
        return getMaxFileDescriptorCount0();
    }

    public double getSystemCpuLoad() {
        if (containerMetrics != null) {
            long quota = containerMetrics.getCpuQuota();
            if (quota > 0) {
                long periodLength = containerMetrics.getCpuPeriod();
                long numPeriods = containerMetrics.getCpuNumPeriods();
                long usageNanos = containerMetrics.getCpuUsage();
                if (periodLength > 0 && numPeriods > 0 && usageNanos > 0) {
                    long elapsedNanos = TimeUnit.MICROSECONDS.toNanos(periodLength * numPeriods);
                    double systemLoad = (double) usageNanos / elapsedNanos;
                    // Ensure the return value is in the range 0.0 -> 1.0
                    systemLoad = Math.max(0.0, systemLoad);
                    systemLoad = Math.min(1.0, systemLoad);
                    return systemLoad;
                }
                return -1;
            } else {
                // If CPU quotas are not active then find the average system load for
                // all online CPUs that are allowed to run this container.

                // If the cpuset is the same as the host's one there is no need to iterate over each CPU
                if (isCpuSetSameAsHostCpuSet()) {
                    return getSystemCpuLoad0();
                } else {
                    int[] cpuSet = containerMetrics.getEffectiveCpuSetCpus();
                    if (cpuSet != null && cpuSet.length > 0) {
                        double systemLoad = 0.0;
                        for (int cpu : cpuSet) {
                            double cpuLoad = getSingleCpuLoad0(cpu);
                            if (cpuLoad < 0) {
                                return -1;
                            }
                            systemLoad += cpuLoad;
                        }
                        return systemLoad / cpuSet.length;
                    }
                    return -1;
                }
            }
        }
        return getSystemCpuLoad0();
    }

    public double getProcessCpuLoad() {
        return getProcessCpuLoad0();
    }

    private boolean isCpuSetSameAsHostCpuSet() {
        if (containerMetrics != null) {
            return containerMetrics.getCpuSetCpus().length == getHostConfiguredCpuCount0();
        }
        return false;
    }

    /* native methods */
    private native long getCommittedVirtualMemorySize0();
    private native long getFreePhysicalMemorySize0();
    private native long getFreeSwapSpaceSize0();
    private native long getMaxFileDescriptorCount0();
    private native long getOpenFileDescriptorCount0();
    private native long getProcessCpuTime0();
    private native double getProcessCpuLoad0();
    private native double getSystemCpuLoad0();
    private native long getTotalPhysicalMemorySize0();
    private native long getTotalSwapSpaceSize0();
    private native double getSingleCpuLoad0(int cpuNum);
    private native int getHostConfiguredCpuCount0();

    static {
        initialize0();
    }

    private static native void initialize0();
}
