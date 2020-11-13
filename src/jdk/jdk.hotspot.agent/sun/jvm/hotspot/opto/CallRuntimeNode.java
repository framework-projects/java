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

import sun.jvm.hotspot.utilities.CStringUtilities;

import java.io.PrintStream;

public class CallRuntimeNode extends CallNode {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("CallRuntimeNode");
    nameField    = type.getAddressField("_name");
  }

  static private AddressField nameField;

  public String name() {
    return CStringUtilities.getString(nameField.getValue(getAddress()));
  }

  public CallRuntimeNode(Address addr) {
    super(addr);
  }

  public void dumpSpec(PrintStream out) {
    out.print(" #");
    out.print(name());
    super.dumpSpec(out);
  }
}
