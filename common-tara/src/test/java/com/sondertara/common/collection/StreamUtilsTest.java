package com.sondertara.common.collection;

import com.sondertara.common.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamUtilsTest {

    @Test
    public void testOfWithArray() {
        Integer[] array = {1, 2, 3};
        List<Integer> expected = Arrays.asList(array);
        List<Integer> result = StreamUtils.of(array).collect(Collectors.toList());
        assertEquals(expected, result);
    }

    @Test
    public void testOfWithIterable() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        List<Integer> expected = list;
        List<Integer> result = StreamUtils.of(list).collect(Collectors.toList());
        assertEquals(expected, result);
    }

    @Test
    public void testOfWithIterator() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        List<Integer> result = StreamUtils.of(list.iterator()).collect(Collectors.toList());
        assertEquals(list, result);
    }


    @Test
    public void testOfEnumeration() throws SocketException {
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        Stream<NetworkInterface> objectStream = StreamUtils.of(enumeration);
        objectStream.forEach(obj -> {
            Assertions.assertNotNull(obj.getName());
        });
    }


    @Test
    public void testOfWithFile() throws Exception {
        // Assuming the file contains one integer per line
        File file = new File("test.txt");
        FileUtils.writeLines(Arrays.asList(1, 2, 3), file, StandardCharsets.UTF_8);

        assertTrue(file.exists());
        try (Stream<String> stream = StreamUtils.of(file)) {
            List<Integer> expected = Arrays.asList(1, 2, 3);
            List<Integer> result = stream.map(Integer::parseInt).collect(Collectors.toList());
            assertEquals(expected, result);
        }
    }

    @Test
    public void testJoinWithStream() {
        List<String> list = Arrays.asList("a", "b", "c");
        String expected = "a,b,c";
        String result = StreamUtils.join(list.stream(), ",");
        assertEquals(expected, result);
    }

    @Test
    public void testJoinWithStreamAndFunction() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        String expected = "1,2,3";
        String result = StreamUtils.join(list.stream(), ",", Object::toString);
        assertEquals(expected, result);
    }

    @Test
    public void testSumWithStream() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        BigDecimal expected = new BigDecimal("6");
        BigDecimal result = StreamUtils.sum(list.stream());
        assertEquals(expected, result);
    }
}
