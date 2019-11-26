package com.sondertara.common.util;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author: maowenbo
 */
public class EnumUtil {

    /**
     * 获取是否类枚举的name
     */
    public static <T extends Enum<T>> String getName(Class<T> enumType, Boolean flag) {
        if (flag == null) {
            return null;
        }
        return getName(enumType, flag ? 1 : 0);
    }

    /**
     * get name, by code
     *
     * @param enumType Enum
     * @param <T>      Enum.class
     * @return name
     */
    public static <T extends Enum<T>> String getName(Class<T> enumType, Integer code) {
        if (code == null) {
            return null;
        }
        //得到enum数组
        T[] enumConstants = enumType.getEnumConstants();
        for (T enumConstant : enumConstants) {
            Class<? extends Enum> aClass = enumConstant.getClass();
            try {
                Field code1 = aClass.getDeclaredField("code");
                Field name = aClass.getDeclaredField("name");
                code1.setAccessible(true);
                name.setAccessible(true);
                Integer value1 = (Integer) code1.get(enumConstant);
                if (value1.equals(code)) {
                    return (String) name.get(enumConstant);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {

            }
        }
        return null;
    }

    public static <T extends Enum<T>> String getName(Class<T> enumType, Integer code,
                                                     String attrName) {
        if (code == null) {
            return null;
        }
        //得到enum数组
        T[] enumConstants = enumType.getEnumConstants();
        for (T enumConstant : enumConstants) {
            Class<? extends Enum> aClass = enumConstant.getClass();
            try {
                Field code1 = aClass.getDeclaredField("code");
                Field name = aClass.getDeclaredField(attrName);
                code1.setAccessible(true);
                name.setAccessible(true);
                Integer value1 = (Integer) code1.get(enumConstant);
                if (value1.equals(code)) {
                    return (String) name.get(enumConstant);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {

            }
        }
        return null;
    }

    /**
     * get enum, by code
     *
     * @param enumType Enum
     * @param code     Enum code
     * @param <T>      Enum.class
     * @return name
     */
    public static <T extends Enum<T>> T getEnumByCode(Class<T> enumType, Integer code) {
        if (code == null) {
            return null;
        }
        //得到enum数组
        T[] enumConstants = enumType.getEnumConstants();
        for (T enumConstant : enumConstants) {
            Class<? extends Enum> aClass = enumConstant.getClass();
            try {
                Field code1 = aClass.getDeclaredField("code");
                code1.setAccessible(true);
                Integer value1 = (Integer) code1.get(enumConstant);
                if (value1.equals(code)) {
                    return enumConstant;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * get Map,key is name,value is code
     */
    public static <T extends Enum<T>> Map<String, Integer> getMap(Class<T> enumType) {
        Map<String, Integer> map = Maps.newHashMap();

        T[] enumConstants = enumType.getEnumConstants();
        for (T enumConstant : enumConstants) {
            Class<? extends Enum> aClass = enumConstant.getClass();
            try {
                Field code = aClass.getDeclaredField("code");
                Field name = aClass.getDeclaredField("name");
                code.setAccessible(true);
                name.setAccessible(true);
                Integer codeValue = (Integer) code.get(enumConstant);
                String nameValue = (String) name.get(enumConstant);
                map.put(nameValue, codeValue);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     *
     */
    public static <T extends Enum<T>, K, V> Map<K, V> getMap(Class<T> enumType, String keyFieldName,
                                                             Class<K> keyType, String valueFieldName, Class<V> valueType) {
        Map<K, V> map = Maps.newHashMap();

        T[] enumConstants = enumType.getEnumConstants();
        for (T enumConstant : enumConstants) {
            Class<? extends Enum> aClass = enumConstant.getClass();
            try {
                Field keyField = aClass.getDeclaredField(keyFieldName);
                Field valueField = aClass.getDeclaredField(valueFieldName);
                keyField.setAccessible(true);
                valueField.setAccessible(true);
                K key = (K) keyField.get(enumConstant);
                V value = (V) valueField.get(enumConstant);
                map.put(key, value);
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                e.printStackTrace();
            }
        }

        return map;
    }


    public static <T extends Enum<T>> List<Map<String, Object>> getList(Class<T> enumType,
                                                                        String codeFieldName, String nameFieldName) {
        List<Map<String, Object>> result = Lists.newArrayList();

        T[] enumConstants = enumType.getEnumConstants();
        for (T enumConstant : enumConstants) {
            Class<? extends Enum> aClass = enumConstant.getClass();
            Map<String, Object> map = Maps.newHashMap();
            try {
                Field codeField = aClass.getDeclaredField(codeFieldName);
                Field nameField = aClass.getDeclaredField(nameFieldName);
                codeField.setAccessible(true);
                nameField.setAccessible(true);
                Object code = codeField.get(enumConstant);
                Object name = nameField.get(enumConstant);
                map.put("code", code);
                map.put("name", name);
            } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
                e.printStackTrace();
            }
            result.add(map);
        }

        return result;
    }
}
