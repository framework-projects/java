/*
 * Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.utilities;

public class HashtableEntry extends BasicHashtableEntry {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) {
    Type type = db.lookupType("IntptrHashtableEntry");
    literalField   = type.getAddressField("_literal");
  }

  // Fields
  private static AddressField      literalField;

  // Accessors
  public Address literalValue() {
    return literalField.getValue(addr);
  }

  public HashtableEntry(Address addr) {
    super(addr);
  }
}