package com.sondertara.common.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ListsTest {

    @Test
    void newArrayList() {
        List<Object> list = Lists.newArrayList(1, "two", 3.0);
        assertEquals(3, list.size(), "List should contain three elements");
        assertEquals(Integer.valueOf(1), list.get(0), "First element should be an Integer 1");
        assertEquals("two", list.get(1), "Second element should be a String 'two'");
        assertEquals(Double.valueOf(3.0), list.get(2), "Third element should be a Double 3.0");
    }

    @Test
    void testNewArrayList() {
        List<Integer> input = Arrays.asList(1, 2, 3);
        List<Integer> result = Lists.newArrayList(input);
        assertEquals(input, result, "The list should contain the same elements as the input iterable of primitives");

    }

    @Test
    void testNewArrayList1() {
    }


    @Test
    void newArrayListWithExpectedSize() {
    }

    @Test
    void newLinkedList() {
        // Arrange
        List<String> expected = new LinkedList<>();
        expected.add("a");
        expected.add(null);
        expected.add("c");

        // Act
        List<String> actual = Lists.newLinkedList("a", null, "c");

        // Assert
        Assertions.assertEquals(expected, actual, "Expected a LinkedList containing the provided elements including null");

    }

    @Test
    void testNewLinkedList() {
        Iterable<Integer> iterable = () -> Arrays.asList(1, 2, 3).iterator();
        Stream<Integer> stream = StreamUtils.of(iterable, false);
        assertNotNull(stream);
        assertFalse(stream.isParallel());
        List<Integer> result = stream.collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3), result);

    }

    @Test
    void testNewLinkedList1() {
    }

    @Test
    void asList() {
    }

    @Test
    void testAsList() {
    }

    @Test
    void testAsList1() {
    }

    @Test
    void testAsList2() {
    }

    @Test
    void testAsList3() {
    }

    @Test
    void getEmptyListIfNull() {
    }

    @Test
    void testGetEmptyListIfNull() {
    }

    @Test
    void newCopyOnWriteArrayList() {
    }

    @Test
    void testNewCopyOnWriteArrayList() {
    }

    @Test
    void testAsList4() {
    }

    @Test
    void testAsList5() {
    }

    @Test
    void immutableList() {
        List<String> expected = Arrays.asList("a", "b", "c");
        List<String> result = Lists.immutableList("a", "b", "c");
        Assertions.assertEquals(expected, result, "List should match the expected values");

        Assertions.assertThrows(UnsupportedOperationException.class, () -> result.add("d"),
                "Adding to an immutable list should throw UnsupportedOperationException");
    }

    @Test
    void testImmutableList() {
    }

    @Test
    void testImmutableList1() {
    }

    @Test
    void testImmutableList2() {
    }

    @Test
    void transform() {
    }

    @Test
    void partition() {
    }

    @Test
    void charactersOf() {
    }

    @Test
    void reverse() {
    }

    @Test
    void hashCodeImpl() {
    }

    @Test
    void addAllImpl() {
    }

    @Test
    void indexOfImpl() {
    }

    @Test
    void lastIndexOfImpl() {
    }

    @Test
    void listIteratorImpl() {
    }

    @Test
    void cast() {
    }
}