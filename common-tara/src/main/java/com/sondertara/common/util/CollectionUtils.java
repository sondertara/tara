package com.sondertara.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * utils
 *
 * @author huangxiaohu
 */
public final class CollectionUtils {

    public static <T> T[] toArray(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T[] array = ((T[]) new Object[list.size()]);
        list.toArray(array);
        return array;
    }

    /**
     * map是否为空
     */
    public static boolean isEmpty(Map<? extends Object, ? extends Object> map) {
        return map == null || map.isEmpty();
    }

    /**
     * map是否不为空
     */
    public static boolean isNotEmpty(Map<? extends Object, ? extends Object> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 将Map转换为List
     */
    public static List<? extends Object> parseMap2List(Map<? extends Object, ? extends Object> map) {
        List<Object> list = null;
        if (isNotEmpty(map)) {
            list = new ArrayList<Object>();
            for (Map.Entry<? extends Object, ? extends Object> entry : map.entrySet()) {
                list.add(entry.getValue());
            }
        }
        return list;
    }

    public static <K, V> Map<K, Object> convertMapValueToObject(Map<K, V> map) {
        Map<K, Object> retMap = new HashMap<K, Object>();
        if (CollectionUtils.isEmpty(map)) {
            return retMap;
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            retMap.put(entry.getKey(), entry.getValue());
        }
        return retMap;
    }

    /**
     * list is empty
     *
     * @param list list
     * @return is empty
     */
    public static boolean isEmpty(Collection<? extends Object> list) {
        return list == null || list.isEmpty();
    }

    /**
     * list is not empty
     *
     * @param list list
     * @return is not empty
     */
    public static boolean isNotEmpty(Collection<? extends Object> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * 把list转换成string，中间以combineChar来连接
     *
     * @param lists       original list
     * @param combineChar char
     * @return strs list
     */
    public static <T> String combineListToString(List<T> lists, char combineChar) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (T l : lists) {
            if (i > 0) {
                sb.append(combineChar);
            }
            i++;
            sb.append(l);
        }

        return sb.toString();
    }

    /**
     * 把list转换成string，中间以combineChar来连接
     *
     * @param lists      list
     * @param combineStr list
     * @return new String
     */
    public static <T> String combineListToString(List<T> lists, String combineStr) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (T l : lists) {
            if (i > 0) {
                sb.append(combineStr);
            }
            i++;
            sb.append(l);
        }

        return sb.toString();
    }

    /**
     * 将String类型数组转成Long类型List
     *
     * @param strArr list
     **/
    public static List<Long> strArrToLongList(String[] strArr) {
        List<Long> result = new ArrayList<Long>();
        if (strArr == null || strArr.length == 0) {
            return result;
        }
        for (String str : strArr) {
            result.add(Long.valueOf(str));
        }
        return result;
    }
}