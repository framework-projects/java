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

package sun.jvm.hotspot.runtime;

public class vmSymbols {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static Address symbolsAddress;
  private static int FIRST_SID;
  private static int SID_LIMIT;

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type            = db.lookupType("vmSymbols");
    symbolsAddress       = type.getAddressField("_symbols[0]").getStaticFieldAddress();
    FIRST_SID            = db.lookupIntConstant("vmSymbols::FIRST_SID");
    SID_LIMIT            = db.lookupIntConstant("vmSymbols::SID_LIMIT");
  }

  public static Symbol symbolAt(int id) {
    if (id < FIRST_SID || id >= SID_LIMIT) throw new IndexOutOfBoundsException("bad SID " + id);
    return Symbol.create(symbolsAddress.getAddressAt(id * VM.getVM().getAddressSize()));
  }
}
