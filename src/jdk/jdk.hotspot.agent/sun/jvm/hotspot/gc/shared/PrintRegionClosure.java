/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.gc.shared;

import sun.jvm.hotspot.gc.g1.HeapRegion;

import java.io.PrintStream;

public class PrintRegionClosure implements SpaceClosure {
  private PrintStream tty;

  public PrintRegionClosure(PrintStream tty) {
    this.tty = tty;
  }

  public void doSpace(Space hr) {
    ((HeapRegion)hr).printOn(tty);
  }
}
