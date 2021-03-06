/*
 * Copyright (c) 2018, 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.lir.hashing;

import jdk.vm.ci.meta.JavaConstant;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.Value;
import org.graalvm.compiler.lir.gen.ArithmeticLIRGenerator;

import java.util.*;

/**
 * This class holds a hash function at a specific cardinality and min value (lowest key). The
 * cardinality is the required size of the hash table to make the hasher injective for the provided
 * keys.
 */
public final class Hasher {

    /**
     * Tries to find a hash function without conflicts for the provided keys.
     *
     * @param keys the keys
     * @param minDensity the minimum density of the switch table. Used to determine the maximum
     *            cardinality of the hash function
     * @return an optional hasher
     */
    public static Optional<Hasher> forKeys(JavaConstant[] keys, double minDensity) {
        assert checkKeyKind(keys);
        if (keys.length <= 2) {
            return Optional.empty();
        } else {
            int maxCardinality = (int) Math.round(keys.length / minDensity);
            assert checkIfSorted(keys);
            TreeSet<Hasher> candidates = new TreeSet<>(new Comparator<Hasher>() {
                @Override
                public int compare(Hasher o1, Hasher o2) {
                    int d = o1.cardinality - o2.cardinality;
                    if (d != 0) {
                        return d;
                    } else {
                        return o1.effort() - o2.effort();
                    }
                }
            });
            int min = keys[0].asInt();
            for (HashFunction f : HashFunction.instances()) {
                for (int cardinality = keys.length; cardinality < maxCardinality; cardinality++) {
                    if (isValid(keys, min, f, cardinality)) {
                        candidates.add(new Hasher(f, cardinality, min));
                        break;
                    }
                }
            }
            if (candidates.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(candidates.first());
            }
        }
    }

    private static boolean checkKeyKind(JavaConstant[] keys) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].getJavaKind() != JavaKind.Int) {
                throw new AssertionError(String.format("Key at index %d is not an int: %s", i, keys[i]));
            }
        }
        return true;
    }

    private static boolean checkIfSorted(JavaConstant[] keys) {
        for (int i = 1; i < keys.length; i++) {
            if (keys[i - 1].asInt() >= keys[i].asInt()) {
                throw new AssertionError("Keys array is not sorted");
            }
        }
        return true;
    }

    private static boolean isValid(JavaConstant[] keys, int min, HashFunction function, int cardinality) {
        Set<Integer> seen = new HashSet<>(keys.length);
        for (JavaConstant key : keys) {
            int hash = function.apply(key.asInt(), min) & (cardinality - 1);
            if (!seen.add(hash)) {
                return false;
            }
        }
        return true;
    }

    private final HashFunction function;
    private final int cardinality;
    private final int min;

    private Hasher(HashFunction function, int cardinality, int min) {
        this.function = function;
        this.cardinality = cardinality;
        this.min = min;
    }

    /**
     * Applies the hash function.
     *
     * @param value the value to be hashed
     * @return the hash value
     */
    public int hash(int value) {
        return function.apply(value, min) & (cardinality - 1);
    }

    /**
     * Applies the hash function to a lir value.
     *
     * @param value the value to be hashed
     * @param gen the lir generator
     * @return the hashed lir value
     */
    public Value hash(Value value, ArithmeticLIRGenerator gen) {
        Value h = function.gen(value, gen.getLIRGen().emitJavaConstant(JavaConstant.forInt(min)), gen);
        return gen.emitAnd(h, gen.getLIRGen().emitJavaConstant(JavaConstant.forInt(cardinality - 1)));
    }

    /**
     * @return the hashing effort
     */
    public int effort() {
        return function.effort() + 1;
    }

    /**
     * @return the cardinality of the hash function that should match the size of the table switch.
     */
    public int cardinality() {
        return cardinality;
    }

    /**
     * @return the hash function
     */
    public HashFunction function() {
        return function;
    }

    @Override
    public String toString() {
        return "Hasher[function=" + function + ", effort=" + effort() + ", cardinality=" + cardinality + "]";
    }
}
