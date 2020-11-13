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

/** Instantiate wrappers for statically typed instances. */

public class StaticBaseConstructor<T> extends InstanceConstructor {
  private Class staticType;

  public StaticBaseConstructor(Class<T> t) {
    staticType = t;
  }

  /** Instantiate a wrapper using staticType */
  public VMObject instantiateWrapperFor(Address addr) throws WrongTypeException {
    if (addr == null) {
      return null;
    }

    return (VMObject) VMObjectFactory.newObject(staticType, addr);
  }
}
