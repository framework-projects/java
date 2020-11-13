/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
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
 */


package org.graalvm.compiler.nodes.memory;

import jdk.internal.vm.compiler.word.LocationIdentity;
import org.graalvm.compiler.nodes.extended.GuardedNode;
import org.graalvm.compiler.nodes.memory.address.AddressNode;

public interface Access extends GuardedNode, HeapAccess {

    AddressNode getAddress();

    void setAddress(AddressNode address);

    LocationIdentity getLocationIdentity();

    boolean canNullCheck();

}
