package com.sondertara.common.collection;


import com.sondertara.common.text.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 驼峰Key风格的Map<br>
 * 对KEY转换为驼峰，get("int_value")和get("intValue")获得的值相同，put进入的值也会被覆盖
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Looly
 * @since 4.0.7
 */
public class CamelCaseMap<K, V> extends FuncKeyMap<K, V> {
    private static final long serialVersionUID = 4043263744224569870L;

    CamelCaseMap(Map<K, V> hashMap) {
        super(hashMap, key -> {
            if (key instanceof CharSequence) {
                key = StringUtils.toCamelCase(key.toString());
            }
            //noinspection unchecked
            return (K) key;
        });
    }

    public static <K, V> CamelCaseMap<K, V> of(Map<K, V> map) {
        return new CamelCaseMap<>(map);
    }


    // ------------------------------------------------------------------------- Constructor start

    /**
     * 构造
     */
    public CamelCaseMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始大小
     */
    public CamelCaseMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }


    /**
     * 构造
     *
     * @param initialCapacity 初始大小
     * @param loadFactor      加载因子
     */
    public CamelCaseMap(int initialCapacity, float loadFactor) {
        this(new HashMap<>(initialCapacity, loadFactor));
    }
}
