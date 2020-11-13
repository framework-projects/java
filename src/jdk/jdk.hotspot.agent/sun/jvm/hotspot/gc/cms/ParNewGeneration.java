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
 */

package sun.jvm.hotspot.gc.cms;

public class ParNewGeneration extends DefNewGeneration {
  public ParNewGeneration(Address addr) {
    super(addr);
  }

  public Generation.Name kind() {
    return Generation.Name.PAR_NEW;
  }
}
