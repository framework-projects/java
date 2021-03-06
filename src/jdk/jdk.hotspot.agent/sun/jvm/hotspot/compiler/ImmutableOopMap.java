/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.compiler;

public class ImmutableOopMap extends VMObject {
  private static CIntegerField countField;
  private static long classSize;

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static void initialize(TypeDataBase db) {
    Type type = db.lookupType("ImmutableOopMap");
    countField = type.getCIntegerField("_count");
    classSize = type.getSize();
  }

  public ImmutableOopMap(Address addr) {
    super(addr);
  }

  //--------------------------------------------------------------------------------
  // Internals only below this point
  //

  long getCount() {
    return countField.getValue(addr);
  }

  public Address getData() {
    return addr.addOffsetTo(classSize);
  }
}
