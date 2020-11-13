/*
 * Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.runtime;

public class ServiceThread extends JavaThread {
  public ServiceThread(Address addr) {
    super(addr);
  }

  public boolean isJavaThread() { return false; }
  public boolean isHiddenFromExternalView() { return true; }
  public boolean isServiceThread() { return true; }

}
