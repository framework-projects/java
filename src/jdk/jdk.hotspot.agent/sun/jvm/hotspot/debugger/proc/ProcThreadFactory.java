/*
 * Copyright (c) 2002, Oracle and/or its affiliates. All rights reserved.
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
 */

package sun.jvm.hotspot.debugger.proc;

/** An interface used only internally by the ProcDebugger to be able to
    create platform-specific Thread objects */

public interface ProcThreadFactory {
  public ThreadProxy createThreadWrapper(Address threadIdentifierAddr);
  public ThreadProxy createThreadWrapper(long id);
}
