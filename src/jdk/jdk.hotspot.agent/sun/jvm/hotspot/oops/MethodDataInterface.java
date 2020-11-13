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

package sun.jvm.hotspot.oops;

public interface MethodDataInterface<K, M> {
  K getKlassAtAddress(Address addr);
  M getMethodAtAddress(Address addr);
  void printKlassValueOn(K klass, PrintStream st);
  void printMethodValueOn(M klass, PrintStream st);
}
