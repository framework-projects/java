/*
 * Copyright (c) 2018, 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.management;

import com.sun.management.ThreadMXBean;
import org.graalvm.compiler.serviceprovider.JMXService;
import org.graalvm.compiler.serviceprovider.ServiceProvider;

import java.lang.management.ManagementFactory;
import java.util.List;

import static java.lang.Thread.currentThread;

/**
 * Implementation of {@link JMXService} for JDK 13 and later.
 */
@ServiceProvider(JMXService.class)
public class JMXServiceProvider extends JMXService {
    private final ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

    @Override
    protected long getThreadAllocatedBytes(long id) {
        return threadMXBean.getThreadAllocatedBytes(id);
    }

    @Override
    protected long getCurrentThreadCpuTime() {
        long[] times = threadMXBean.getThreadCpuTime(new long[]{currentThread().getId()});
        return times[0];
    }

    @Override
    protected boolean isThreadAllocatedMemorySupported() {
        return threadMXBean.isThreadAllocatedMemorySupported();
    }

    @Override
    protected boolean isCurrentThreadCpuTimeSupported() {
        return threadMXBean.isThreadCpuTimeSupported();
    }

    @Override
    protected List<String> getInputArguments() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments();
    }
}
