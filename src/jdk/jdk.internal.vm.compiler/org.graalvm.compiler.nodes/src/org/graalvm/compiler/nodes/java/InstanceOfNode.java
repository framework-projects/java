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

import jdk.vm.ci.meta.*;
import org.graalvm.compiler.core.common.type.ObjectStamp;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.core.common.type.TypeReference;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.CanonicalizerTool;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.calc.ConditionalNode;
import org.graalvm.compiler.nodes.calc.IsNullNode;
import org.graalvm.compiler.nodes.extended.AnchoringNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.spi.Lowerable;
import org.graalvm.compiler.nodes.spi.LoweringTool;
import org.graalvm.compiler.nodes.spi.Virtualizable;
import org.graalvm.compiler.nodes.spi.VirtualizerTool;
import org.graalvm.compiler.nodes.type.StampTool;

import java.util.Objects;

import static org.graalvm.compiler.nodeinfo.InputType.Anchor;
import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_8;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_8;

/**
 * The {@code InstanceOfNode} represents an instanceof test.
 */
@NodeInfo(cycles = CYCLES_8, size = SIZE_8)
public class InstanceOfNode extends UnaryOpLogicNode implements Lowerable, Virtualizable {
    public static final NodeClass<InstanceOfNode> TYPE = NodeClass.create(InstanceOfNode.class);

    private final ObjectStamp checkedStamp;

    private JavaTypeProfile profile;
    @OptionalInput(Anchor) protected AnchoringNode anchor;

    private InstanceOfNode(ObjectStamp checkedStamp, ValueNode object, JavaTypeProfile profile, AnchoringNode anchor) {
        this(TYPE, checkedStamp, object, profile, anchor);
    }

    protected InstanceOfNode(NodeClass<? extends InstanceOfNode> c, ObjectStamp checkedStamp, ValueNode object, JavaTypeProfile profile, AnchoringNode anchor) {
        super(c, object);
        this.checkedStamp = checkedStamp;
        this.profile = profile;
        this.anchor = anchor;
        assert (profile == null) || (anchor != null) : "profiles must be anchored";
        assert checkedStamp != null;
        assert type() != null;
    }

    public static LogicNode createAllowNull(TypeReference type, ValueNode object, JavaTypeProfile profile, AnchoringNode anchor) {
        if (StampTool.isPointerNonNull(object)) {
            return create(type, object, profile, anchor);
        }
        return createHelper(StampFactory.object(type), object, profile, anchor);
    }

    public static LogicNode create(TypeReference type, ValueNode object) {
        return create(type, object, null, null);
    }

    public static LogicNode create(TypeReference type, ValueNode object, JavaTypeProfile profile, AnchoringNode anchor) {
        return createHelper(StampFactory.objectNonNull(type), object, profile, anchor);
    }

    public static LogicNode createHelper(ObjectStamp checkedStamp, ValueNode object, JavaTypeProfile profile, AnchoringNode anchor) {
        LogicNode synonym = findSynonym(checkedStamp, object, NodeView.DEFAULT);
        if (synonym != null) {
            return synonym;
        } else {
            return new InstanceOfNode(checkedStamp, object, profile, anchor);
        }
    }

    @Override
    public void lower(LoweringTool tool) {
        tool.getLowerer().lower(this, tool);
    }

    @Override
    public ValueNode canonical(CanonicalizerTool tool, ValueNode forValue) {
        NodeView view = NodeView.from(tool);
        LogicNode synonym = findSynonym(checkedStamp, forValue, view);
        if (synonym != null) {
            return synonym;
        } else {
            return this;
        }
    }

    public static LogicNode findSynonym(ObjectStamp checkedStamp, ValueNode object, NodeView view) {
        ObjectStamp inputStamp = (ObjectStamp) object.stamp(view);
        ObjectStamp joinedStamp = (ObjectStamp) checkedStamp.join(inputStamp);

        if (joinedStamp.isEmpty()) {
            // The check can never succeed, the intersection of the two stamps is empty.
            return LogicConstantNode.contradiction();
        } else {
            ObjectStamp meetStamp = (ObjectStamp) checkedStamp.meet(inputStamp);
            if (checkedStamp.equals(meetStamp)) {
                // The check will always succeed, the union of the two stamps is equal to the
                // checked stamp.
                return LogicConstantNode.tautology();
            } else if (checkedStamp.alwaysNull()) {
                return IsNullNode.create(object);
            } else if (Objects.equals(checkedStamp.type(), meetStamp.type()) && checkedStamp.isExactType() == meetStamp.isExactType() && checkedStamp.alwaysNull() == meetStamp.alwaysNull()) {
                assert checkedStamp.nonNull() != inputStamp.nonNull();
                // The only difference makes the null-ness of the value => simplify the check.
                if (checkedStamp.nonNull()) {
                    return LogicNegationNode.create(IsNullNode.create(object));
                } else {
                    return IsNullNode.create(object);
                }
            }
            assert checkedStamp.type() != null;
        }
        return null;
    }

    /**
     * Gets the type being tested.
     */
    public TypeReference type() {
        return StampTool.typeReferenceOrNull(checkedStamp);
    }

    public JavaTypeProfile profile() {
        return profile;
    }

    @Override
    public void virtualize(VirtualizerTool tool) {
        ValueNode alias = tool.getAlias(getValue());
        TriState fold = tryFold(alias.stamp(NodeView.DEFAULT));
        if (fold != TriState.UNKNOWN) {
            tool.replaceWithValue(LogicConstantNode.forBoolean(fold.isTrue(), graph()));
        }
    }

    @Override
    public Stamp getSucceedingStampForValue(boolean negated) {
        if (negated) {
            return null;
        } else {
            return checkedStamp;
        }
    }

    @Override
    public TriState tryFold(Stamp valueStamp) {
        if (valueStamp instanceof ObjectStamp) {
            ObjectStamp inputStamp = (ObjectStamp) valueStamp;
            ObjectStamp joinedStamp = (ObjectStamp) checkedStamp.join(inputStamp);

            if (joinedStamp.isEmpty()) {
                // The check can never succeed, the intersection of the two stamps is empty.
                return TriState.FALSE;
            } else {
                ObjectStamp meetStamp = (ObjectStamp) checkedStamp.meet(inputStamp);
                if (checkedStamp.equals(meetStamp)) {
                    // The check will always succeed, the union of the two stamps is equal to the
                    // checked stamp.
                    return TriState.TRUE;
                }
            }
        }
        return TriState.UNKNOWN;
    }

    public boolean allowsNull() {
        return !checkedStamp.nonNull();
    }

    public void setProfile(JavaTypeProfile typeProfile, AnchoringNode anchor) {
        this.profile = typeProfile;
        updateUsagesInterface(this.anchor, anchor);
        this.anchor = anchor;
        assert (profile == null) || (anchor != null) : "profiles must be anchored";
    }

    public AnchoringNode getAnchor() {
        return anchor;
    }

    public ObjectStamp getCheckedStamp() {
        return checkedStamp;
    }

    @NodeIntrinsic
    public static native boolean doInstanceof(@ConstantNodeParameter ResolvedJavaType type, Object object);

    @SuppressWarnings("unused")
    static boolean intrinsify(GraphBuilderContext b, ResolvedJavaMethod method, ResolvedJavaType type, ValueNode object) {
        InstanceOfNode node = new InstanceOfNode(StampFactory.objectNonNull(TypeReference.create(b.getAssumptions(), type)), object, null, null);
        node = b.add(node);
        b.addPush(JavaKind.Int, ConditionalNode.create(node, NodeView.DEFAULT));
        return true;
    }

    @Override
    public TriState implies(boolean thisNegated, LogicNode other) {
        if (other instanceof InstanceOfNode) {
            InstanceOfNode instanceOfNode = (InstanceOfNode) other;
            if (instanceOfNode.getValue() == getValue()) {
                if (thisNegated) {
                    // !X => Y
                    if (this.getCheckedStamp().meet(instanceOfNode.getCheckedStamp()).equals(this.getCheckedStamp())) {
                        return TriState.get(false);
                    }
                } else {
                    // X => Y
                    if (instanceOfNode.getCheckedStamp().meet(this.getCheckedStamp()).equals(instanceOfNode.getCheckedStamp())) {
                        return TriState.get(true);
                    }
                }
            }
        }
        return super.implies(thisNegated, other);
    }
}