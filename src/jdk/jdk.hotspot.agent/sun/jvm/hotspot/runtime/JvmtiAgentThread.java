/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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

/** FIXME: should be in ../prims dir if that directory existed; for
    now keep it in runtime dir */

public class JvmtiAgentThread extends JavaThread {
  public JvmtiAgentThread(Address addr) {
    super(addr);
  }

  public boolean isJavaThread() { return false; }

  public boolean isJvmtiAgentThread() { return true; }

}
