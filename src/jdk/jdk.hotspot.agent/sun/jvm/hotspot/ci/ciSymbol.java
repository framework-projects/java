/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.ci;

public class ciSymbol extends ciMetadata {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type = db.lookupType("ciSymbol");
    symbolField = type.getAddressField("_symbol");
  }

  private static AddressField symbolField;

  public String asUtf88() {
    Symbol sym = Symbol.create(symbolField.getValue(getAddress()));
    return sym.asString();
  }

  public ciSymbol(Address addr) {
    super(addr);
  }
}
