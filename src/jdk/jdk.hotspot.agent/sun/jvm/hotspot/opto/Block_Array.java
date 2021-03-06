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

public class Block_Array extends VMObject {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("Block_Array");
    sizeField = new CIntField(type.getCIntegerField("_size"), 0);
    blocksField = type.getAddressField("_blocks");
    arenaField = type.getAddressField("_arena");
  }

  private static CIntField sizeField;
  private static AddressField blocksField;
  private static AddressField arenaField;

  public Block_Array(Address addr) {
    super(addr);
  }

  public int Max() {
    return (int) sizeField.getValue(getAddress());
  }

  public Block at(int i) {
    return new Block(blocksField.getValue(getAddress()).getAddressAt(i * (int)VM.getVM().getAddressSize()));
  }
}
