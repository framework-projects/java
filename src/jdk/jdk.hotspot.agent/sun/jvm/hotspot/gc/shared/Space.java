/*
 * Copyright (c) 2000, 2015, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.gc.shared;

/** <P> A Space describes a heap area. Class Space is an abstract base
    class. </P>

    <P> Space supports allocation, size computation and GC support is
    provided. </P>

    <P> Invariant: bottom() and end() are on page_size boundaries and: </P>

    <P> bottom() <= top() <= end() </P>

    <P> top() is inclusive and end() is exclusive. </P> */

public abstract class Space extends VMObject {
  private static AddressField bottomField;
  private static AddressField endField;

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) {
    Type type = db.lookupType("Space");

    bottomField = type.getAddressField("_bottom");
    endField    = type.getAddressField("_end");
  }

  public Space(Address addr) {
    super(addr);
  }

  public Address   bottom()       { return bottomField.getValue(addr); }
  public Address   end()          { return endField.getValue(addr);    }

  /** Returns a subregion of the space containing all the objects in
      the space. */
  public MemRegion usedRegion() {
    return new MemRegion(bottom(), end());
  }

  /** Support for iteration over heap -- not sure how this will
      interact with GC in reflective system, but necessary for the
      debugging mechanism */
  public OopHandle bottomAsOopHandle() {
    return bottomField.getOopHandle(addr);
  }

  /** Support for iteration over heap -- not sure how this will
      interact with GC in reflective system, but necessary for the
      debugging mechanism */
  public OopHandle nextOopHandle(OopHandle handle, long size) {
    return handle.addOffsetToAsOopHandle(size);
  }

  /** returns all MemRegions where live objects are */
  public abstract List/*<MemRegion>*/ getLiveRegions();

  /** Returned value is in bytes */
  public long capacity() { return end().minus(bottom()); }
  /** Returned value is in bytes */
  public abstract long used();
  /** Returned value is in bytes */
  public abstract long free();

  /** Testers */
  public boolean contains(Address p) {
    return (bottom().lessThanOrEqual(p) && end().greaterThan(p));
  }

  public void print() { printOn(System.out); }
  public void printOn(PrintStream tty) {
    tty.print(" space capacity = ");
    tty.print(capacity());
    tty.print(", ");
    tty.print((double) used() * 100.0/ capacity());
    tty.print(" used");
  }
}