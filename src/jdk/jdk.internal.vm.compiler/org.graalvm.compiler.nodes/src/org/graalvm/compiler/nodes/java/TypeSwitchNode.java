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

import jdk.vm.ci.meta.Constant;
import jdk.vm.ci.meta.ConstantReflectionProvider;
import jdk.vm.ci.meta.ResolvedJavaType;
import org.graalvm.compiler.core.common.type.*;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.spi.Simplifiable;
import org.graalvm.compiler.graph.spi.SimplifierTool;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.*;
import org.graalvm.compiler.nodes.extended.LoadHubNode;
import org.graalvm.compiler.nodes.extended.SwitchNode;
import org.graalvm.compiler.nodes.spi.LIRLowerable;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;
import org.graalvm.compiler.nodes.util.GraphUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The {@code TypeSwitchNode} performs a lookup based on the type of the input value. The type
 * comparison is an exact type comparison, not an instanceof.
 */
@NodeInfo
public final class TypeSwitchNode extends SwitchNode implements LIRLowerable, Simplifiable {

    public static final NodeClass<TypeSwitchNode> TYPE = NodeClass.create(TypeSwitchNode.class);
    protected final ResolvedJavaType[] keys;
    protected final Constant[] hubs;

    public TypeSwitchNode(ValueNode value, AbstractBeginNode[] successors, ResolvedJavaType[] keys, double[] keyProbabilities, int[] keySuccessors, ConstantReflectionProvider constantReflection) {
        super(TYPE, value, successors, keySuccessors, keyProbabilities);
        assert successors.length <= keys.length + 1;
        assert keySuccessors.length == keyProbabilities.length;
        this.keys = keys;
        assert value.stamp(NodeView.DEFAULT) instanceof AbstractPointerStamp;
        assert assertKeys();

        hubs = new Constant[keys.length];
        for (int i = 0; i < hubs.length; i++) {
            hubs[i] = constantReflection.asObjectHub(keys[i]);
        }
    }

    /**
     * Don't allow duplicate keys.
     */
    private boolean assertKeys() {
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < keys.length; j++) {
                if (i == j) {
                    continue;
                }
                assert !keys[i].equals(keys[j]);
            }
        }
        return true;
    }

    @Override
    public boolean isSorted() {
        return false;
    }

    @Override
    public int keyCount() {
        return keys.length;
    }

    @Override
    public Constant keyAt(int index) {
        return hubs[index];
    }

    @Override
    public boolean equalKeys(SwitchNode switchNode) {
        if (!(switchNode instanceof TypeSwitchNode)) {
            return false;
        }
        TypeSwitchNode other = (TypeSwitchNode) switchNode;
        return Arrays.equals(keys, other.keys);
    }

    public ResolvedJavaType typeAt(int index) {
        return keys[index];
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        gen.emitSwitch(this);
    }

    @Override
    public void simplify(SimplifierTool tool) {
        NodeView view = NodeView.from(tool);
        if (value() instanceof ConstantNode) {
            Constant constant = value().asConstant();

            int survivingEdge = keySuccessorIndex(keyCount());
            for (int i = 0; i < keyCount(); i++) {
                Constant typeHub = keyAt(i);
                Boolean equal = tool.getConstantReflection().constantEquals(constant, typeHub);
                if (equal == null) {
                    /* We don't know if this key is a match or not, so we cannot simplify. */
                    return;
                } else if (equal.booleanValue()) {
                    survivingEdge = keySuccessorIndex(i);
                }
            }
            killOtherSuccessors(tool, survivingEdge);
        }
        if (value() instanceof LoadHubNode && ((LoadHubNode) value()).getValue().stamp(view) instanceof ObjectStamp) {
            ObjectStamp objectStamp = (ObjectStamp) ((LoadHubNode) value()).getValue().stamp(view);
            if (objectStamp.type() != null) {
                int validKeys = 0;
                for (int i = 0; i < keyCount(); i++) {
                    if (objectStamp.type().isAssignableFrom(keys[i])) {
                        validKeys++;
                    }
                }
                if (validKeys == 0) {
                    tool.addToWorkList(defaultSuccessor());
                    graph().removeSplitPropagate(this, defaultSuccessor());
                } else if (validKeys != keys.length) {
                    ArrayList<AbstractBeginNode> newSuccessors = new ArrayList<>(blockSuccessorCount());
                    ResolvedJavaType[] newKeys = new ResolvedJavaType[validKeys];
                    int[] newKeySuccessors = new int[validKeys + 1];
                    double[] newKeyProbabilities = new double[validKeys + 1];
                    double totalProbability = 0;
                    int current = 0;
                    for (int i = 0; i < keyCount() + 1; i++) {
                        if (i == keyCount() || objectStamp.type().isAssignableFrom(keys[i])) {
                            int index = newSuccessors.indexOf(keySuccessor(i));
                            if (index == -1) {
                                index = newSuccessors.size();
                                newSuccessors.add(keySuccessor(i));
                            }
                            newKeySuccessors[current] = index;
                            if (i < keyCount()) {
                                newKeys[current] = keys[i];
                            }
                            newKeyProbabilities[current] = keyProbability(i);
                            totalProbability += keyProbability(i);
                            current++;
                        }
                    }
                    if (totalProbability > 0) {
                        for (int i = 0; i < current; i++) {
                            newKeyProbabilities[i] /= totalProbability;
                        }
                    } else {
                        for (int i = 0; i < current; i++) {
                            newKeyProbabilities[i] = 1.0 / current;
                        }
                    }

                    ArrayList<AbstractBeginNode> oldSuccessors = new ArrayList<>();
                    for (int i = 0; i < blockSuccessorCount(); i++) {
                        AbstractBeginNode successor = blockSuccessor(i);
                        oldSuccessors.add(successor);
                        setBlockSuccessor(i, null);
                    }

                    AbstractBeginNode[] successorsArray = newSuccessors.toArray(new AbstractBeginNode[newSuccessors.size()]);
                    TypeSwitchNode newSwitch = graph().add(new TypeSwitchNode(value(), successorsArray, newKeys, newKeyProbabilities, newKeySuccessors, tool.getConstantReflection()));
                    ((FixedWithNextNode) predecessor()).setNext(newSwitch);
                    GraphUtil.killWithUnusedFloatingInputs(this);

                    for (int i = 0; i < oldSuccessors.size(); i++) {
                        AbstractBeginNode successor = oldSuccessors.get(i);
                        if (!newSuccessors.contains(successor)) {
                            GraphUtil.killCFG(successor);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Stamp getValueStampForSuccessor(AbstractBeginNode beginNode) {
        Stamp result = null;
        if (beginNode != defaultSuccessor()) {
            for (int i = 0; i < keyCount(); i++) {
                if (keySuccessor(i) == beginNode) {
                    if (result == null) {
                        result = StampFactory.objectNonNull(TypeReference.createExactTrusted(typeAt(i)));
                    } else {
                        result = result.meet(StampFactory.objectNonNull(TypeReference.createExactTrusted(typeAt(i))));
                    }
                }
            }
        }
        return result;
    }
}
