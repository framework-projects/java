/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.runtime.ppc64;

public class PPC64JavaCallWrapper extends JavaCallWrapper {

  public PPC64JavaCallWrapper(Address addr) {
    super(addr);
  }

  public Address getLastJavaFP() {
    return null;
  }

}
