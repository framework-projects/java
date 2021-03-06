/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.util.test;

import org.graalvm.util.CollectionsUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class CollectionUtilTest {

    private static int sum(Iterable<Integer> iterable) {
        int sum = 0;
        for (int i : iterable) {
            sum += i;
        }
        return sum;
    }

    private static int indexOf(Iterable<Integer> iterable, int element) {
        int index = 0;
        for (int i : iterable) {
            if (i == element) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Test
    public void testConcat() {
        List<Integer> a = Arrays.asList(1, 2);
        List<Integer> b = Arrays.asList(3, 4, 5);
        Assert.assertEquals(sum(CollectionsUtil.concat(a, b)), 15);
        Assert.assertEquals(sum(CollectionsUtil.concat(b, a)), 15);
        Assert.assertEquals(indexOf(CollectionsUtil.concat(a, b), 5), 4);
        Assert.assertEquals(indexOf(CollectionsUtil.concat(b, a), 5), 2);
    }

    @Test
    public void testMatch() {
        String[] array = {"a", "b", "c", "d", "e"};
        Assert.assertTrue(CollectionsUtil.allMatch(array, s -> !s.isEmpty()));
        Assert.assertFalse(CollectionsUtil.allMatch(array, s -> !s.startsWith("c")));
        Assert.assertFalse(CollectionsUtil.anyMatch(array, String::isEmpty));
        Assert.assertTrue(CollectionsUtil.anyMatch(array, s -> s.startsWith("c")));
    }

    @Test
    public void testFilterToList() {
        String[] array = {"a", "b", "", "d", "e"};
        Assert.assertEquals(CollectionsUtil.filterToList(Arrays.asList(array), String::isEmpty).size(), 1);
    }

    @Test
    public void testFilterAndMapToArray() {
        String[] array = {"a", "b", "", "d", "e"};
        String[] newArray = CollectionsUtil.filterAndMapToArray(array, s -> !s.isEmpty(), String::toUpperCase, String[]::new);
        Assert.assertArrayEquals(newArray, new String[]{"A", "B", "D", "E"});
    }

    @Test
    public void testMapToArray() {
        String[] array = {"a", "b", "c", "d", "e"};
        String[] newArray = CollectionsUtil.mapToArray(array, String::toUpperCase, String[]::new);
        Assert.assertArrayEquals(newArray, new String[]{"A", "B", "C", "D", "E"});
    }

    @Test
    public void testMapAndJoin() {
        String[] array = {"a", "b", "c", "d", "e"};
        Assert.assertEquals(CollectionsUtil.mapAndJoin(array, String::toUpperCase, ", "), "A, B, C, D, E");
        Assert.assertEquals(CollectionsUtil.mapAndJoin(array, String::toUpperCase, ", ", "'"), "'A, 'B, 'C, 'D, 'E");
        Assert.assertEquals(CollectionsUtil.mapAndJoin(array, String::toUpperCase, ", ", "'", "'"), "'A', 'B', 'C', 'D', 'E'");

        Assert.assertEquals(CollectionsUtil.mapAndJoin(Arrays.asList(array), String::toUpperCase, ", "), "A, B, C, D, E");
        Assert.assertEquals(CollectionsUtil.mapAndJoin(Arrays.asList(array), String::toUpperCase, ", ", "'"), "'A, 'B, 'C, 'D, 'E");
    }

    @Test
    public void testIterableConcat() {
        List<String> i1 = Arrays.asList("1", "2", "3");
        List<String> i2 = Arrays.asList();
        List<String> i3 = Arrays.asList("4", "5");
        List<String> i4 = Arrays.asList();
        List<String> i5 = Arrays.asList("6");
        List<String> iNull = null;

        List<String> actual = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        expected.addAll(i1);
        expected.addAll(i2);
        expected.addAll(i3);
        expected.addAll(i4);
        expected.addAll(i5);
        Iterable<String> iterable = CollectionsUtil.concat(Arrays.asList(i1, i2, i3, i4, i5));
        for (String s : iterable) {
            actual.add(s);
        }
        Assert.assertEquals(expected, actual);

        Iterator<String> iter = iterable.iterator();
        while (iter.hasNext()) {
            iter.next();
        }
        try {
            iter.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // Expected
        }
        try {
            CollectionsUtil.concat(i1, iNull);
            Assert.fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        Iterable<Object> emptyIterable = CollectionsUtil.concat(Collections.emptyList());
        Assert.assertFalse(emptyIterable.iterator().hasNext());
    }

}
