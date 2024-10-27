package com.sondertara.common;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * ObjectUtilsTest
 *
 * @author huangxiaohu.1ih
 * @date 2024/7/24 15:29
 */
class ObjectUtilsTest {

    @Test
    void testAnyNull() {
        boolean result = ObjectUtils.anyNull("aaa", null, "fff");
        Assertions.assertEquals(true, result);
    }

    @Test
    void testAllNotNull() {
        boolean result = ObjectUtils.allNotNull("aa", "bbb");
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIsEmpty() {
        ArrayList<Object> list = new ArrayList<>();
        boolean result = ObjectUtils.isEmpty(list);
        Assertions.assertEquals(true, result);
    }

    @Test
    void testEquals() {
        int[] a = new int[]{1, 2, 3};
        int[] b = new int[]{1, 2, 3};
        boolean result = ObjectUtils.equals(a, b);
        Assertions.assertEquals(true, result);
    }

    @Test
    void testNotEqual() {
        int[] a = new int[]{1, 2, 3};
        int[] b = new int[]{1, 3, 2};
        boolean result = ObjectUtils.notEqual(a, b);
        Assertions.assertEquals(true, result);
    }

    @Test
    void testLength() {
        ArrayList<Object> list = new ArrayList<>();
        list.add("1");
        list.add(null);
        int result = ObjectUtils.length(list);
        Assertions.assertEquals(2, result);
    }

    @Test
    void testContains() {
        Map<String, Integer> obj = new HashMap<>();
        obj.put("a", 1);
        obj.put("b", 2);
        boolean result = ObjectUtils.contains(obj, 1);
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIsNull() {
        String s = null;
        boolean result = ObjectUtils.isNull(null);
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIsNotNull() {
        String s = "11";
        boolean result = ObjectUtils.isNotNull(s);
        Assertions.assertEquals(true, result);
    }

    @Test
    void testDefaultIfNull() {
        String result = ObjectUtils.defaultIfNull(null, "10");
        Assertions.assertEquals("10", result);
    }

    @Test
    void testDefaultIfNull2() {
        String result = ObjectUtils.defaultIfNull(null, new Supplier<String>() {
            @Override
            public String get() {
                return "A";
            }
        });
        Assertions.assertEquals("A", result);
    }


    @Test
    void testDefaultIfEmpty2() {
        String result = ObjectUtils.defaultIfEmpty("a", "1");
        Assertions.assertEquals("a", result);
    }

    @Test
    void testDefaultIfEmpty3() {
        String result = ObjectUtils.defaultIfEmpty(null, new Supplier<String>() {
            @Override
            public String get() {
                return "a";
            }
        });
        Assertions.assertEquals("a", result);
    }

    @Test
    void testDefaultIfBlank() {
        String result = ObjectUtils.defaultIfBlank(" ", "ab");
        Assertions.assertEquals("ab", result);
    }


    @Test
    void testIsBasicType() {
        Integer object = 1;
        boolean result = ObjectUtils.isBasicType(object);
        Assertions.assertEquals(true, result);
        BigDecimal bigDecimal = new BigDecimal("1");
        boolean result1 = ObjectUtils.isBasicType(bigDecimal);
        Assertions.assertEquals(false, result1);
    }


    @Test
    void testGetIdentity() {
        String result = ObjectUtils.getIdentity("aaa");
    }

    @Test
    void testHashCode() {
        int result = ObjectUtils.hashCode("aa");
    }

    @Test
    void testToString() {
        String result = ObjectUtils.toString("f");
        Assertions.assertEquals("f", result);
    }

    @Test
    void testSerialize() {
        byte[] result = ObjectUtils.serialize(new BigDecimal("1"));
        BigDecimal bigDecimal = ObjectUtils.deserialize(result);
        Assertions.assertEquals(1, bigDecimal.intValue());
    }

    @Test
    void testClone() {
        int[] array = new int[]{1, 2, 3};
        int[] clone = ObjectUtils.clone(array);
        Assertions.assertTrue(ArrayUtils.equals(array, clone));
    }

    @Test
    void testRequireNonNull() {

        Assertions.assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                ObjectUtils.requireNonNull(null, new Supplier<String>() {
                    @Override
                    public String get() {
                        return "target is null";
                    }
                });
            }
        });
    }


    @Test
    void testIsNotEmpty() {
        boolean result = ObjectUtils.isNotEmpty("a");
        Assertions.assertEquals(true, result);
    }


    @Test
    void testId() {
        int result = ObjectUtils.id("kkkj");
    }

    @Test
    void testIdentityToString() {
        String result = ObjectUtils.identityToString("ffff");
    }

    @Test
    void testIdentityHashCodeHex() {
        String result = ObjectUtils.identityHashCodeHex("ffff");
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme