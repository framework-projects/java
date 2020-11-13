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

package sun.jvm.hotspot.ci;

public class ciObjArrayKlass extends ciArrayKlass {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("ciObjArrayKlass");
    elementKlassField = type.getAddressField("_element_klass");
    baseElementKlassField = type.getAddressField("_base_element_klass");
  }

  private static AddressField elementKlassField;
  private static AddressField baseElementKlassField;

  public ciObjArrayKlass(Address addr) {
    super(addr);
  }
}
