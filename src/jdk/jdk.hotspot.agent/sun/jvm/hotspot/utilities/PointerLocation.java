/*
 * Copyright (c) 2000, 2018, Oracle and/or its affiliates. All rights reserved.
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

/** This class attempts to describe possible locations of pointers in
    the VM. */

public class PointerLocation {
  //////////////////////////////////////////////////////////////////
  //                                                              //
  // These are package private to simplify the implementation and //
  // interaction with PointerFinder                               //
  //                                                              //
  //////////////////////////////////////////////////////////////////

  Address addr;

  CollectedHeap heap;
  Generation gen;

  // If UseTLAB was enabled and the pointer was found in a
  // currently-active TLAB, these will be set
  boolean inTLAB;
  JavaThread tlabThread;
  ThreadLocalAllocBuffer tlab;

  // Generated code locations
  boolean inInterpreter;
  boolean inCodeCache;

  // FIXME: add other locations like VTableStubs, StubRoutines, maybe
  // even "on thread x's stack"

  InterpreterCodelet interpreterCodelet;
  CodeBlob blob;
  // FIXME: add more detail about CodeBlob
  boolean inBlobCode;
  boolean inBlobData;
  boolean inBlobOops;
  boolean inBlobUnknownLocation;

  boolean inStrongGlobalJNIHandles;
  boolean inWeakGlobalJNIHandles;

  boolean inLocalJNIHandleBlock;
  JNIHandleBlock handleBlock;
  sun.jvm.hotspot.runtime.Thread handleThread;

  public PointerLocation(Address addr) {
    this.addr = addr;
  }

  public boolean isInHeap() {
    return (heap != null || (gen != null));
  }

  public boolean isInNewGen() {
    return ((gen != null) && (gen == ((GenCollectedHeap)heap).getGen(0)));
  }

  public boolean isInOldGen() {
    return ((gen != null) && (gen == ((GenCollectedHeap)heap).getGen(1)));
  }

  public boolean inOtherGen() {
    return (!isInNewGen() && !isInOldGen());
  }

  /** Only valid if isInHeap() */
  public Generation getGeneration() {
      return gen;
  }

  /** This may be true if isInNewGen is also true */
  public boolean isInTLAB() {
    return inTLAB;
  }

  /** Only valid if isInTLAB() returns true */
  public JavaThread getTLABThread() {
    return tlabThread;
  }

  /** Only valid if isInTLAB() returns true */
  public ThreadLocalAllocBuffer getTLAB() {
    return tlab;
  }

  public boolean isInInterpreter() {
    return inInterpreter;
  }

  /** For now, only valid if isInInterpreter is true */
  public InterpreterCodelet getInterpreterCodelet() {
    return interpreterCodelet;
  }

  public boolean isInCodeCache() {
    return inCodeCache;
  }

  /** For now, only valid if isInCodeCache is true */
  public CodeBlob getCodeBlob() {
    return blob;
  }

  public boolean isInBlobCode() {
    return inBlobCode;
  }

  public boolean isInBlobData() {
    return inBlobData;
  }

  public boolean isInBlobOops() {
    return inBlobOops;
  }

  public boolean isInBlobUnknownLocation() {
    return inBlobUnknownLocation;
  }

  public boolean isInStrongGlobalJNIHandles() {
    return inStrongGlobalJNIHandles;
  }

  public boolean isInWeakGlobalJNIHandles() {
    return inWeakGlobalJNIHandles;
  }

  public boolean isInLocalJNIHandleBlock() {
    return inLocalJNIHandleBlock;
  }

  /** Only valid if isInLocalJNIHandleBlock is true */
  public JNIHandleBlock getJNIHandleBlock() {
    assert isInLocalJNIHandleBlock();
    return handleBlock;
  }

  /** Only valid if isInLocalJNIHandleBlock is true */
  public sun.jvm.hotspot.runtime.Thread getJNIHandleThread() {
    assert isInLocalJNIHandleBlock();
    return handleThread;
  }

  public boolean isUnknown() {
    return (!(isInHeap() || isInInterpreter() || isInCodeCache() ||
              isInStrongGlobalJNIHandles() || isInWeakGlobalJNIHandles() || isInLocalJNIHandleBlock()));
  }

  public String toString() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    printOn(new PrintStream(bos));
    return bos.toString();
  }

  public void print() {
    printOn(System.out);
  }

  public void printOn(PrintStream tty) {
    tty.print("Address ");
    if (addr == null) {
      tty.print("0x0");
    } else {
      tty.print(addr.toString());
    }
    tty.print(": ");
    if (isInHeap()) {
      if (isInTLAB()) {
        tty.print("In thread-local allocation buffer for thread \"" +
                  getTLABThread().getThreadName() + "\" (");
        getTLABThread().printThreadIDOn(tty);
        tty.print(") ");
        getTLAB().printOn(tty);
      } else {
        if (isInNewGen()) {
          tty.print("In new generation ");
        } else if (isInOldGen()) {
          tty.print("In old generation ");
        } else {
          tty.print("In unknown section of Java heap");
        }
        if (getGeneration() != null) {
          getGeneration().printOn(tty);
        }
      }
    } else if (isInInterpreter()) {
      tty.println("In interpreter codelet \"" + interpreterCodelet.getDescription() + "\"");
      interpreterCodelet.printOn(tty);
    } else if (isInCodeCache()) {
      CodeBlob b = getCodeBlob();
      tty.print("In ");
      if (isInBlobCode()) {
        tty.print("code");
      } else if (isInBlobData()) {
        tty.print("data");
      } else if (isInBlobOops()) {
        tty.print("oops");
      } else {
        if (Assert.ASSERTS_ENABLED) {
          Assert.that(isInBlobUnknownLocation(), "Should have known location in CodeBlob");
        }
        tty.print("unknown location");
      }
      tty.print(" in ");
      b.printOn(tty);

      // FIXME: add more detail
    } else if (isInStrongGlobalJNIHandles()) {
      tty.print("In JNI strong global");
    } else if (isInWeakGlobalJNIHandles()) {
      tty.print("In JNI weak global");
    } else if (isInLocalJNIHandleBlock()) {
      tty.print("In thread-local");
      tty.print(" JNI handle block (" + handleBlock.top() + " handle slots present)");
      if (handleThread.isJavaThread()) {
        tty.print(" for JavaThread ");
        ((JavaThread) handleThread).printThreadIDOn(tty);
      } else {
        tty.print(" for a non-Java Thread");
      }
    } else {
      // This must be last
      if (Assert.ASSERTS_ENABLED) {
        Assert.that(isUnknown(), "Should have unknown location");
      }
      tty.print("In unknown location");
    }
  }
}
