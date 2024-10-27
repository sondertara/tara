package com.sondertara.common.collection;

import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionUtilsTest {

    @Test
    void toArray() {
        List<String> list = Arrays.asList("a", "b", "c");
        String[] array = ArrayUtils.toArray(list, String.class);
        Assertions.assertNotNull(array, "The array should not be null");
        assertEquals("a", array[0], "First element should match");
        assertEquals("b", array[1], "Second element should match");
        assertEquals("c", array[2], "Third element should match");
    }

    @Test
    public void testAsCollectionWithCollectionIterable() {
        // Given: a Collection Iterable
        List<String> inputList = Arrays.asList("one", "two", "three");
        Iterable<String> iterable = inputList;

        // When: asCollection is called with the Collection Iterable
        Collection<String> result = CollectionUtils.asCollection(iterable);

        // Then: the result is the same as the input Collection
        Assertions.assertIterableEquals(inputList, result);

    }

    @Test
    public void testAsCollectionWithIterableContainingNulls() {
        // Given: an Iterable containing null values
        Iterable<String> iterable = Arrays.asList("first", null, "third");

        // When: asCollection is called with the Iterable
        Collection<String> result = CollectionUtils.asCollection(iterable);

        // Then: the result contains nulls as well
        assertEquals(3, result.size());
        Assertions.assertTrue(result.contains(null));
    }

    @Test
    void testToCollectWithIterableContainingNulls() {
        Function<Character, Character> mapper = s -> s;
        Collection<Character> result = CollectionUtils.toCollect("nullable", mapper);
        assertEquals(Arrays.asList('n','u','l','l','a','b','l','e'), result, "Result should include nulls as they are present in the iterable");
    }

    @Test
    void testMin() {
        class CustomObject {

            @Getter
            int value;

            CustomObject(int value) {
                this.value = value;
            }
        }

        Comparator<CustomObject> comparator = Comparator.comparingInt(CustomObject::getValue);
        List<CustomObject> customObjectList = Lists.newArrayList(new CustomObject(3), new CustomObject(2), new CustomObject(4));
        assertEquals(2, CollectionUtils.min(customObjectList, comparator).value);
    }

    @Test
    void isEmpty() {
    }

    @Test
    void isNotEmpty() {
    }

    @Test
    void testIsEmpty() {
    }

    @Test
    void testIsEmpty1() {
    }

    @Test
    void testIsNotEmpty() {
    }

    @Test
    void join() {
    }

    @Test
    void testJoin() {
    }

    @Test
    void strArrToLongList() {
    }

    @Test
    void emptyNonAbsentHashMap() {
    }

    @Test
    void wrapAsNonAbsentMap() {
    }

    @Test
    void getEmptyMapIfNull() {
    }

    @Test
    void testGetEmptyMapIfNull() {
    }

    @Test
    void asCollection() {
    }

    @Test
    void testToArray() {
    }

    @Test
    void asArray() {
    }

    @Test
    void testToArray1() {
    }

    @Test
    void asIterable() {
    }

    @Test
    void testAsIterable() {
    }

    @Test
    void filter() {
    }

    @Test
    void testFilter() {
    }

    @Test
    void testFilter1() {
    }

    @Test
    void testFilter2() {
    }

    @Test
    void map() {
    }

    @Test
    void testMap() {
    }

    @Test
    void testMap1() {
    }

    @Test
    void testMap2() {
    }

    @Test
    void flat() {
    }

    @Test
    void flatMap() {
    }

    @Test
    void testFlatMap() {
    }

    @Test
    void forEach() {
    }

    @Test
    void testForEach() {
    }

    @Test
    void testForEach1() {
    }

    @Test
    void testForEach2() {
    }

    @Test
    void testForEach3() {
    }

    @Test
    void testForEach4() {
    }

    @Test
    void testForEach5() {
    }

    @Test
    void testForEach6() {
    }

    @Test
    void testForEach7() {
    }

    @Test
    void testForEach8() {
    }

    @Test
    void testForEach9() {
    }

    @Test
    void testForEach10() {
    }

    @Test
    void testForEach11() {
    }

    @Test
    void testForEach12() {
    }

    @Test
    void testForEach13() {
    }

    @Test
    void testForEach14() {
    }

    @Test
    void testForEach15() {
    }

    @Test
    void testForEach16() {
    }

    @Test
    void testForEach17() {
    }

    @Test
    void testForEach18() {
    }

    @Test
    void testForEach19() {
    }

    @Test
    void firstOccurrence() {
    }

    @Test
    void testFirstOccurrence() {
    }

    @Test
    void firstMap() {
    }

    @Test
    void testFirstMap() {
    }

    @Test
    void testFirstMap1() {
    }

    @Test
    void testFirstMap2() {
    }

    @Test
    void testFirstMap3() {
    }

    @Test
    void findFirst() {
    }

    @Test
    void testFindFirst() {
    }

    @Test
    void testFindFirst1() {
    }

    @Test
    void findN() {
    }

    @Test
    void findNPairs() {
    }

    @Test
    void testFindN() {
    }

    @Test
    void removeIf() {
    }

    @Test
    void removeAll() {
    }

    @Test
    void testRemoveIf() {
    }

    @Test
    void testRemoveIf1() {
    }

    @Test
    void anyMatch() {
    }

    @Test
    void testAnyMatch() {
    }

    @Test
    void testAnyMatch1() {
    }

    @Test
    void allMatch() {
    }

    @Test
    void testAllMatch() {
    }

    @Test
    void testAllMatch1() {
    }

    @Test
    void noneMatch() {
    }

    @Test
    void testNoneMatch() {
    }

    @Test
    void testNoneMatch1() {
    }

    @Test
    void distinct() {
    }

    @Test
    void limit() {
        String[] query = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        String[] strings = CollectionUtils.limit(query, 3);
        Assertions.assertNotNull(strings, "The array should not be null");
        Assertions.assertNotNull(strings, "The array should not be null");
        Assertions.assertArrayEquals(strings, new String[]{"a", "b", "c"});
    }

    @Test
    void testLimit() {
    }

    @Test
    void skip() {
        String[] query = new String[]{"a", "b", "c", "d", "e", "f", "g"};
        String[] strings = CollectionUtils.skip(query, 7);
        Assertions.assertNotNull(strings, "The array should not be null");
        Assertions.assertArrayEquals(strings, new String[]{});


    }

    @Test
    void testSkip() {
    }

    @Test
    void partitionBy() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<List<Integer>> lists = CollectionUtils.partitionBy(list, i -> i % 2 == 0);
        Assertions.assertNotNull(lists, "The array should not be null");
        assertEquals(2, lists.size(), "The array size is error");

        List<String> asList = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");

    }

    @Test
    void testPartitionBy() {
    }

    @Test
    void testPartitionBy1() {
    }

    @Test
    void testPartitionBy2() {
    }

    @Test
    void testPartitionBy3() {
    }

    @Test
    void testPartitionBy4() {
    }

    @Test
    void partitionByCount() {
    }

    @Test
    void partitionBySize() {
    }

    @Test
    void sort() {
    }

    @Test
    void testSort() {
    }

    @Test
    void testSort1() {
    }

    @Test
    void testSort2() {
    }

    @Test
    void reverse() {
    }

    @Test
    void testReverse() {
    }

    @Test
    void count() {
    }

    @Test
    void testCount() {
    }

    @Test
    void testCount1() {
    }

    @Test
    void max() {
    }

    @Test
    void min() {
    }

    @Test
    void collect() {
    }

    @Test
    void testCollect() {
    }

    @Test
    void testCollect1() {
    }

    @Test
    void diff() {
    }

    @Test
    void testDiff() {
    }

    @Test
    void testDiff1() {
    }

    @Test
    void testDiff2() {
    }

    @Test
    void testDiff3() {
    }

    @Test
    void testDiff4() {
    }

    @Test
    void testDiff5() {
    }

    @Test
    void testDiff6() {
    }

    @Test
    void testDiff7() {
    }

    @Test
    void addAll() {
    }

    @Test
    void testAddAll() {
    }

    @Test
    void testAddAll1() {
    }

    @Test
    void addTo() {
    }

    @Test
    void testAddTo() {
    }

    @Test
    void concat() {
    }

    @Test
    void testConcat() {
    }

    @Test
    void copy() {
    }

    @Test
    void merge() {
    }

    @Test
    void testMerge() {
    }

    @Test
    void testMerge1() {
    }

    @Test
    void testMerge2() {
    }

    @Test
    void containsAny() {
    }

    @Test
    void contains() {
    }

    @Test
    void testContains() {
    }

    @Test
    void testContains1() {
    }

    @Test
    void testContains2() {
    }

    @Test
    void containsAll() {
    }

    @Test
    void containsNone() {
    }

    @Test
    void intersection() {
    }

    @Test
    void union() {
    }

    @Test
    void reduce() {
    }

    @Test
    void testReduce() {
    }

    @Test
    void groupBy() {
    }

    @Test
    void testGroupBy() {
    }

    @Test
    void testGroupBy1() {
    }

    @Test
    void testGroupBy2() {
    }

    @Test
    void groupingBy() {
    }

    @Test
    void testGroupingBy() {
    }

    @Test
    void partitioningBy() {
    }

    @Test
    void testPartitioningBy() {
    }

    @Test
    void shuffle() {
    }

    @Test
    void testShuffle() {
    }

    @Test
    void swap() {
    }

    @Test
    void testSwap() {
    }

    @Test
    void indexOf() {
    }

    @Test
    void testIndexOf() {
    }

    @Test
    void testIndexOf1() {
    }

    @Test
    void lastIndexOf() {
    }

    @Test
    void testLastIndexOf() {
    }

    @Test
    void testLastIndexOf1() {
    }

    @Test
    void testIndexOf2() {
    }

    @Test
    void testIndexOf3() {
    }

    @Test
    void testIndexOf4() {
    }

    @Test
    void testLastIndexOf2() {
    }

    @Test
    void testLastIndexOf3() {
    }

    @Test
    void testLastIndexOf4() {
    }

    @Test
    void apply() {
    }

    @Test
    void isFirst() {
    }

    @Test
    void isLast() {
    }

    @Test
    void testIsFirst() {
    }

    @Test
    void testIsLast() {
    }

    @Test
    void testIsFirst1() {
    }

    @Test
    void testIsLast1() {
    }
}