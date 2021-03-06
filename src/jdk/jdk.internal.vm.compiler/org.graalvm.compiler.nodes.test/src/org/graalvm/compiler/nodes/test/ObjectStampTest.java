/*
 * Copyright (c) 2014, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.nodes.test;

import org.graalvm.compiler.core.common.type.AbstractObjectStamp;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.core.common.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

public class ObjectStampTest extends AbstractObjectStampTest {
    @Test
    public void testInterfaceTrust0() {
        Stamp notTrusted = StampFactory.object(TypeReference.createWithoutAssumptions(getMetaAccess().lookupJavaType(I.class)));
        Assert.assertEquals(StampFactory.object(), notTrusted);
    }

    private interface TrustedI {

    }

    @Test
    public void testInterfaceTrust1() {
        Stamp trusted = StampFactory.object(getType(TrustedI.class));
        Assert.assertNotEquals(StampFactory.object(), trusted);
        Assert.assertTrue("Should be an AbstractObjectStamp", trusted instanceof AbstractObjectStamp);
        AbstractObjectStamp trustedObjectStamp = (AbstractObjectStamp) trusted;
        Assert.assertNotNull(trustedObjectStamp.type());
        Assert.assertTrue("Should be an interface", trustedObjectStamp.type().isInterface());
    }
}
