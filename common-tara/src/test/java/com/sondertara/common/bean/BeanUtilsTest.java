package com.sondertara.common.bean;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanUtilsTest {


    @Test
    void testCopyProperties() {
        // Setup
        // Run the test
        BeanUtils.copyProperties("source", "target", "ignoreProperties");

        // Verify the results
    }

    @Data
    public static class Test1 {
        private Integer[] ids;
    }

    @Data
    public static class Test2 {
        private String[] ids;
    }
    @Data
    public static class Test3 {
        private List<String> ids;
    }

    @Test
    void testBeanToBean() {
        assertEquals("source", BeanUtils.beanToBean("source", String.class));

        Test1 test1 = new Test1();
        test1.setIds(new Integer[]{1, 2, 3});
        Test2 beanToBean = BeanUtils.beanToBean(test1, Test2.class);
        assertEquals(Arrays.toString(new String[]{"1", "2", "3"}), Arrays.toString(beanToBean.getIds()));
        Test3 test3 = BeanUtils.beanToBean(test1, Test3.class);
        assertEquals(test3.getIds().toString(), Arrays.toString(beanToBean.getIds()));
    }

    @Test
    void testBeansToBeans() {
        List<String> value = Arrays.asList("value");
        assertEquals(Arrays.asList("value"), BeanUtils.beansToBeans(value, String.class));
    }

    @Test
    void testBeanToMap() {
        // Setup
        // Run the test
        final Map<String, Object> result = BeanUtils.beanToMap("bean");

        // Verify the results
    }

    @Test
    void testMapToBean1() {
        // Setup
        final Map<?, ?> map = new HashMap<>();

        // Run the test
        BeanUtils.mapToBean(map, "t");

        // Verify the results
    }

    @Test
    void testMapToBean2() {

    }

    @Test
    void testBeansToMaps() {
        // Setup
        // Run the test
        final List<Map<String, Object>> result = BeanUtils.beansToMaps(Arrays.asList("value"));

        // Verify the results
    }

    @Test
    void testMapsToBeans() {

    }
}
