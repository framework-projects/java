/*
 * Copyright (c) 2001, 2012, Oracle and/or its affiliates. All rights reserved.
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

public class CheckedExceptionElement {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type            = db.lookupType("CheckedExceptionElement");
    offsetOfClassCPIndex = type.getCIntegerField("class_cp_index").getOffset();
  }

  private static long offsetOfClassCPIndex;

  private Address   handle;
  private long      offset;

  public CheckedExceptionElement(Address handle, long offset) {
    this.handle = handle;
    this.offset = offset;
  }

  public int getClassCPIndex() {
    return (int) handle.getCIntegerAt(offset + offsetOfClassCPIndex, 2, true);
  }
}
