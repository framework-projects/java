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


package org.graalvm.compiler.nodes.extended;

import jdk.internal.vm.compiler.word.LocationIdentity;
import jdk.vm.ci.meta.Assumptions;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.ResolvedJavaField;
import jdk.vm.ci.meta.ResolvedJavaType;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.Canonicalizable;
import org.graalvm.compiler.graph.spi.CanonicalizerTool;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.FixedWithNextNode;
import org.graalvm.compiler.nodes.NamedLocationIdentity;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.type.StampTool;

import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_2;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_1;

@NodeInfo(cycles = CYCLES_2, size = SIZE_1)
public abstract class UnsafeAccessNode extends FixedWithNextNode implements Canonicalizable {

    public static final NodeClass<UnsafeAccessNode> TYPE = NodeClass.create(UnsafeAccessNode.class);
    @Input ValueNode object;
    @Input ValueNode offset;
    protected final JavaKind accessKind;
    protected final LocationIdentity locationIdentity;
    protected final boolean forceAnyLocation;

    protected UnsafeAccessNode(NodeClass<? extends UnsafeAccessNode> c, Stamp stamp, ValueNode object, ValueNode offset, JavaKind accessKind, LocationIdentity locationIdentity,
                    boolean forceAnyLocation) {
        super(c, stamp);
        this.forceAnyLocation = forceAnyLocation;
        assert accessKind != null;
        assert locationIdentity != null;
        this.object = object;
        this.offset = offset;
        this.accessKind = accessKind;
        this.locationIdentity = locationIdentity;
    }

    public LocationIdentity getLocationIdentity() {
        return locationIdentity;
    }

    public boolean isAnyLocationForced() {
        return forceAnyLocation;
    }

    public ValueNode object() {
        return object;
    }

    public ValueNode offset() {
        return offset;
    }

    public JavaKind accessKind() {
        return accessKind;
    }

    @Override
    public Node canonical(CanonicalizerTool tool) {
        if (!isAnyLocationForced() && getLocationIdentity().isAny()) {
            if (offset().isConstant()) {
                long constantOffset = offset().asJavaConstant().asLong();

                // Try to canonicalize to a field access.
                ResolvedJavaType receiverType = StampTool.typeOrNull(object());
                if (receiverType != null) {
                    ResolvedJavaField field = receiverType.findInstanceFieldWithOffset(constantOffset, accessKind());
                    // No need for checking that the receiver is non-null. The field access includes
                    // the null check and if a field is found, the offset is so small that this is
                    // never a valid access of an arbitrary address.
                    if (field != null && field.getJavaKind() == this.accessKind()) {
                        assert !graph().isAfterFloatingReadPhase() : "cannot add more precise memory location after floating read phase";
                        // Unsafe accesses never have volatile semantics.
                        // Memory barriers are placed around such an unsafe access at construction
                        // time if necessary, unlike AccessFieldNodes which encapsulate their
                        // potential volatile semantics.
                        return cloneAsFieldAccess(graph().getAssumptions(), field, false);
                    }
                }
            }
            ResolvedJavaType receiverType = StampTool.typeOrNull(object());
            // Try to build a better location identity.
            if (receiverType != null && receiverType.isArray()) {
                LocationIdentity identity = NamedLocationIdentity.getArrayLocation(receiverType.getComponentType().getJavaKind());
                assert !graph().isAfterFloatingReadPhase() : "cannot add more precise memory location after floating read phase";
                return cloneAsArrayAccess(offset(), identity);
            }
        }

        return this;
    }

    protected ValueNode cloneAsFieldAccess(Assumptions assumptions, ResolvedJavaField field) {
        return cloneAsFieldAccess(assumptions, field, field.isVolatile());
    }

    protected abstract ValueNode cloneAsFieldAccess(Assumptions assumptions, ResolvedJavaField field, boolean volatileAccess);

    protected abstract ValueNode cloneAsArrayAccess(ValueNode location, LocationIdentity identity);
}
