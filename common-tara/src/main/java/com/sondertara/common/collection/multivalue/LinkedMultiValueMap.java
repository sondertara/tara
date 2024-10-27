package com.sondertara.common.collection.multivalue;


import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.function.Predicate2;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Simple implementation of {@link MultiValueMap} that wraps a {@link LinkedHashMap},
 * storing multiple values in a {@link LinkedList}.
 *
 * <p>This Map implementation is generally not thread-safe. It is primarily designed
 * for data structures exposed from request objects, for use in a single thread only.
 *
 * @param <K> the key type
 * @param <V> the value element type
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 */
public class LinkedMultiValueMap<K, V> extends CommonMultiValueMap<K, V> implements Serializable, Cloneable {

    private static final long serialVersionUID = 3801124242820219131L;
    public static final LinkedMultiValueMap EMPTY = new LinkedMultiValueMap<>();


    /**
     * Create a new LinkedMultiValueMap that wraps a {@link LinkedHashMap}.
     */
    public LinkedMultiValueMap() {
        this(16);
    }

    /**
     * Create a new LinkedMultiValueMap that wraps a {@link LinkedHashMap}
     * with the given initial capacity.
     *
     * @param initialCapacity the initial capacity
     */
    public LinkedMultiValueMap(int initialCapacity) {
        super(new LinkedHashMap<K, Collection<V>>(initialCapacity), input -> Collections.emptyList());
    }

    /**
     * Copy constructor: Create a new LinkedMultiValueMap with the same mappings as
     * the specified Map. Note that this will be a shallow copy; its value-holding
     * List entries will get reused and therefore cannot get modified independently.
     *
     * @param otherMap the Map whose mappings are to be placed in this Map
     * @see #clone()
     * @see #deepCopy()
     */
    public LinkedMultiValueMap(Map<K, List<V>> otherMap) {
        this(otherMap == null ? 16 : otherMap.size());
        putAll(otherMap);
    }

    public LinkedMultiValueMap(MultiValueMap<K,V> otherMap){
        this(otherMap == null ? 16 : otherMap.size());
        putAll(otherMap);
    }

    @Override
    public boolean containsValue(final Object value) {
        return CollectionUtils.anyMatch(this.targetMap, new Predicate2<K, Collection<V>>() {
            @Override
            public boolean test(K key, Collection<V> values) {
                return values.contains(value);
            }
        });
    }


    /**
     * Create a deep copy of this Map.
     *
     * @return a copy of this Map, including a copy of each value-holding List entry
     * (consistently using an independent modifiable {@link LinkedList} for each entry)
     * along the lines of {@code MultiValueMap.addAll} semantics
     * @see #addAll(MultiValueMap)
     * @see #clone()
     */
    public LinkedMultiValueMap<K, V> deepCopy() {
        final LinkedMultiValueMap<K, V> copy = new LinkedMultiValueMap<K, V>(this.targetMap.size());
        CollectionUtils.forEach(targetMap, new BiConsumer<K, Collection<V>>() {
            @Override
            public void accept(K key, Collection<V> values) {
                copy.put(key, Lists.newArrayList(values));
            }
        });
        return copy;
    }

    /**
     * Create a regular copy of this Map.
     *
     * @return a shallow copy of this Map, reusing this Map's value-holding List entries
     * (even if some entries are shared or unmodifiable) along the lines of standard
     * {@code Map.put} semantics
     * @see #putAll(Map)
     * @see LinkedMultiValueMap#LinkedMultiValueMap(Map)
     * @see #deepCopy()
     */
    @Override
    @SuppressWarnings("unchecked")
    public LinkedMultiValueMap<K, V> clone() {
        try {
            return (LinkedMultiValueMap)super.clone();
        }catch (Throwable e){
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof LinkedMultiValueMap)){
            return false;
        }
        return ObjectUtils.equals(this.targetMap,obj);
    }

    @Override
    public int hashCode() {
        return this.targetMap.hashCode();
    }

    @Override
    public String toString() {
        return this.targetMap.toString();
    }

}
