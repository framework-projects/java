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

import java.io.PrintStream;

public class MachSafePointNode extends MachReturnNode {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type      = db.lookupType("MachSafePointNode");
    jvmsField = type.getAddressField("_jvms");
    jvmadjField = new CIntField(type.getCIntegerField("_jvmadj"), 0);
  }

  private static AddressField jvmsField;
  private static CIntField jvmadjField;

  public MachSafePointNode(Address addr) {
    super(addr);
  }

  public JVMState jvms() {
    return JVMState.create(jvmsField.getValue(getAddress()));
  }

  public void dumpSpec(PrintStream out) {
    try {
      JVMState jvms = jvms();
      if (jvms != null) out.print(" !");
      if (jvms == null) out.print("empty jvms");
      while (jvms != null) {
        Method m = jvms.method().method();
        int bci = jvms.bci();
        out.print(" " + m.getMethodHolder().getName().asString().replace('/', '.') + "::" + m.getName().asString() + " @ bci:" + bci);
        jvms = jvms.caller();
      }
    } catch (Exception e) {
      out.print(e);
    }
  }
}
