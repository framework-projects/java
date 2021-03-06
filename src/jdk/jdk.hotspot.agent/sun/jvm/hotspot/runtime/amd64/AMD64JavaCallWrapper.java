/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.runtime.amd64;

public class AMD64JavaCallWrapper extends JavaCallWrapper {
  private static AddressField lastJavaFPField;

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) {
    Type type = db.lookupType("JavaFrameAnchor");

    lastJavaFPField  = type.getAddressField("_last_Java_fp");
  }

  public AMD64JavaCallWrapper(Address addr) {
    super(addr);
  }

  public Address getLastJavaFP() {
    return lastJavaFPField.getValue(addr.addOffsetTo(anchorField.getOffset()));
  }
}
