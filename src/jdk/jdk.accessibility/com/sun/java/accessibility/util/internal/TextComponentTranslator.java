/*
 * Copyright (c) 2002, 2015, Oracle and/or its affiliates. All rights reserved.
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
 *
 */

package com.sun.java.accessibility.util.internal;

/**
 * <p>The Translator class provides a translation to interface Accessible
 * for objects that do not implement interface Accessible.  Assistive
 * technologies can use the 'getAccessible' class method of Translator to
 * obtain an object that implements interface Accessible.  If the object
 * passed in already implements interface Accessible, getAccessible merely
 * returns the object.
 *
 * <p>An example of how an assistive technology might use the Translator
 * class is as follows:
 *
 * <PRE>
 *    Accessible accessible = Translator.getAccessible(someObj);
 *    // obtain information from the 'accessible' object.
 * </PRE>
 *
 * <P>This class extends the Translator class to provide specific support
 * for the TextComponent class.  Translator.getAccessible() will automatically
 * load this class when an assistive technology asks for an accessible
 * translator for TextComponent.
 *
 */
public class TextComponentTranslator extends Translator {

    public AccessibleRole getAccessibleRole() {
        return AccessibleRole.TEXT;
    }
}