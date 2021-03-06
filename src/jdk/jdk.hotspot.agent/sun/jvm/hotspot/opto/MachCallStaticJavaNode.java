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

public class MachCallStaticJavaNode extends MachCallJavaNode {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("MachCallStaticJavaNode");
    nameField    = type.getAddressField("_name");
  }

  private static AddressField nameField;

  public String name() {
    return CStringUtilities.getString(nameField.getValue(getAddress()));
  }

  public MachCallStaticJavaNode(Address addr) {
    super(addr);
  }

  public void dumpSpec(PrintStream st) {
    st.print("Static ");
    String n = name();
    if (n != null) {
      st.printf("wrapper for: %s", n);
      // dump_trap_args(st);
      st.print(" ");
    }
    super.dumpSpec(st);
  }
}
