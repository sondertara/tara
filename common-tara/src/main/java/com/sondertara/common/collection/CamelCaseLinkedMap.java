package com.sondertara.common.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 驼峰Key风格的LinkedHashMap<br>
 * 对KEY转换为驼峰，get("int_value")和get("intValue")获得的值相同，put进入的值也会被覆盖
 *
 * @author Looly
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 4.0.7
 */
public class CamelCaseLinkedMap<K, V> extends CamelCaseMap<K, V> {
	private static final long serialVersionUID = 4043263744224569870L;


	// ------------------------------------------------------------------------- Constructor start
	/**
	 * 构造
	 */
	public CamelCaseLinkedMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	public static <K, V> CamelCaseLinkedMap<K, V> of(Map<K, V> map) {
		return new CamelCaseLinkedMap<>(new LinkedHashMap<>(map));
	}



	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 */
	public CamelCaseLinkedMap(int initialCapacity) {
		this(new LinkedHashMap<>(initialCapacity, DEFAULT_LOAD_FACTOR));
	}




	 CamelCaseLinkedMap(Map<K,V> linkedMap) {
		super(linkedMap);
	}
	// ------------------------------------------------------------------------- Constructor end
}
