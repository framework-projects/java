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

// Type entry used for return from a call. A single cell to record the
// type.
public class ReturnTypeEntry<K,M> extends TypeEntries<K,M> {
  static final int cellCount = 1;

  ReturnTypeEntry(MethodDataInterface<K,M> methodData, ProfileData pd, int baseOff) {
    super(methodData, pd, baseOff);
  }

  K type() {
    return validKlass(baseOff);
  }

  static int staticCellCount() {
    return cellCount;
  }

  int typeIndex() {
    return baseOff;
  }

  void printDataOn(PrintStream st) {
    pd.tab(st);
    printKlass(st, baseOff);
    st.println();
  }
}
