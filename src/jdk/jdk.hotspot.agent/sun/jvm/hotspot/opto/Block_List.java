/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.opto;

public class Block_List extends Block_Array {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("Block_List");
    cntField = new CIntField(type.getCIntegerField("_cnt"), 0);
  }

  private static CIntField cntField;

  public Block_List(Address addr) {
    super(addr);
  }

  public int size() {
    return (int) cntField.getValue(getAddress());
  }
}
