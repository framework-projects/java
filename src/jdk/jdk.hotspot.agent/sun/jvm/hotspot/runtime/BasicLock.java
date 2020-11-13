/*
 * Copyright (c) 2001, Oracle and/or its affiliates. All rights reserved.
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

public class BasicLock extends VMObject {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type  = db.lookupType("BasicLock");
    displacedHeaderField = type.getCIntegerField("_displaced_header");
  }

  private static CIntegerField displacedHeaderField;

  public BasicLock(Address addr) {
    super(addr);
  }

  public Mark displacedHeader() {
    return new Mark(addr.addOffsetTo(displacedHeaderField.getOffset()));
  }
}