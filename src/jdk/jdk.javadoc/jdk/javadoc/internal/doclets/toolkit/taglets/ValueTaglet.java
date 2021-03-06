/*
 * Copyright (c) 2001, 2018, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.toolkit.taglets;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.Messages;
import jdk.javadoc.internal.doclets.toolkit.util.CommentHelper;
import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import java.util.EnumSet;

import static com.sun.source.doctree.DocTree.Kind.VALUE;

/**
 * An inline Taglet representing the value tag. This tag should only be used with
 * constant fields that have a value.  It is used to access the value of constant
 * fields.  This inline tag has an optional field name parameter.  If the name is
 * specified, the constant value is retrieved from the specified field.  A link
 * is also created to the specified field.  If a name is not specified, the value
 * is retrieved for the field that the inline tag appears on.  The name is specifed
 * in the following format:  [fully qualified class name]#[constant field name].
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @author Jamie Ho
 */

public class ValueTaglet extends BaseTaglet {

    /**
     * Construct a new ValueTaglet.
     */
    public ValueTaglet() {
        super(VALUE.tagName, true,
                EnumSet.of(Site.OVERVIEW, Site.PACKAGE, Site.TYPE, Site.CONSTRUCTOR,
                    Site.METHOD, Site.FIELD)); // not Site.MODULE at this time!
    }

    /**
     * Returns the referenced field or a null if the value tag
     * is empty or the reference is invalid.
     *
     * @param holder the tag holder.
     * @param config the  configuration of the doclet.
     * @param tag the value tag.
     *
     * @return the referenced field or null.
     */
    private VariableElement getVariableElement(Element holder, BaseConfiguration config, DocTree tag) {
        CommentHelper ch = config.utils.getCommentHelper(holder);
        String signature = ch.getReferencedSignature(tag);

        Element e = signature == null
                ? holder
                : ch.getReferencedMember(config, tag);

        return (e != null && config.utils.isVariableElement(e))
                ? (VariableElement) e
                : null;
    }

    @Override
    public Content getTagletOutput(Element holder, DocTree tag, TagletWriter writer) {
        Utils utils = writer.configuration().utils;
        Messages messages = writer.configuration().getMessages();
        VariableElement field = getVariableElement(holder, writer.configuration(), tag);
        if (field == null) {
            if (tag.toString().isEmpty()) {
                //Invalid use of @value
                messages.warning(holder,
                        "doclet.value_tag_invalid_use");
            } else {
                //Reference is unknown.
                messages.warning(holder,
                        "doclet.value_tag_invalid_reference", tag.toString());
            }
        } else if (field.getConstantValue() != null) {
            return writer.valueTagOutput(field,
                utils.constantValueExpresion(field),
                // TODO: investigate and cleanup
                // in the j.l.m world, equals will not be accurate
                // !field.equals(tag.holder())
                !utils.elementsEqual(field, holder)
            );
        } else {
            //Referenced field is not a constant.
            messages.warning(holder,
                "doclet.value_tag_invalid_constant", utils.getSimpleName(field));
        }
        return writer.getOutputInstance();
    }
}
