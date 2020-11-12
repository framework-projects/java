/*
 * Copyright (c) 2009, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.nodes.java;

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_2;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_1;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_2;

import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodeinfo.NodeSize;
import org.graalvm.compiler.nodeinfo.Verbosity;
import org.graalvm.compiler.nodes.FixedWithNextNode;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.spi.Lowerable;
import org.graalvm.compiler.nodes.spi.LoweringTool;

import jdk.vm.ci.meta.ResolvedJavaField;

/**
 * The base class of all instructions that access fields.
 */
@NodeInfo(cycles = CYCLES_2, size = SIZE_1)
public abstract class AccessFieldNode extends FixedWithNextNode implements Lowerable {

    public static final NodeClass<AccessFieldNode> TYPE = NodeClass.create(AccessFieldNode.class);
    @OptionalInput ValueNode object;

    protected final ResolvedJavaField field;
    protected final boolean volatileAccess;

    public ValueNode object() {
        return object;
    }

    /**
     * Constructs a new access field object.
     *
     * @param object the instruction producing the receiver object
     * @param field the compiler interface representation of the field
     * @param volatileAccess specifies if the access is volatile or not, this overrides the field
     *            volatile modifier.
     */
    public AccessFieldNode(NodeClass<? extends AccessFieldNode> c, Stamp stamp, ValueNode object, ResolvedJavaField field, boolean volatileAccess) {
        super(c, stamp);
        this.object = object;
        this.field = field;
        this.volatileAccess = volatileAccess;
    }

    /**
     * Constructs a new access field object.
     *
     * @param object the instruction producing the receiver object
     * @param field the compiler interface representation of the field
     */
    public AccessFieldNode(NodeClass<? extends AccessFieldNode> c, Stamp stamp, ValueNode object, ResolvedJavaField field) {
        this(c, stamp, object, field, field.isVolatile());
    }

    /**
     * Gets the compiler interface field for this field access.
     *
     * @return the compiler interface field for this field access
     */
    public ResolvedJavaField field() {
        return field;
    }

    /**
     * Checks whether this field access is an access to a static field.
     *
     * @return {@code true} if this field access is to a static field
     */
    public boolean isStatic() {
        return field.isStatic();
    }

    /**
     * Checks whether this access has volatile semantics.
     *
     * The field access semantics are coupled to the access and not to the field. e.g. it's possible
     * to access volatile fields using non-volatile semantics via VarHandles.
     */
    public boolean isVolatile() {
        return volatileAccess;
    }

    @Override
    public void lower(LoweringTool tool) {
        tool.getLowerer().lower(this, tool);
    }

    @Override
    public String toString(Verbosity verbosity) {
        if (verbosity == Verbosity.Name) {
            return super.toString(verbosity) + "#" + field.getName();
        } else {
            return super.toString(verbosity);
        }
    }

    @Override
    public boolean verify() {
        assertTrue((object == null) == isStatic(), "static field must not have object, instance field must have object");
        return super.verify();
    }

    @Override
    public NodeSize estimatedNodeSize() {
        if (isVolatile()) {
            return SIZE_2;
        }
        return super.estimatedNodeSize();
    }
}
