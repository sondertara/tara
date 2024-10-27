package com.sondertara.common.collection.multivalue;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.struct.counter.SimpleIntegerCounter;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommonMultiValueMap<K, V> implements MultiValueMap<K, V> {
    /**
     * 数据存储结构
     */
    protected Map<K, Collection<V>> targetMap;
    /**
     * 当获取一个不存在的key时，会自动的调用 valuesSupplier 创建一个 Collection
     */
    private Function<K, Collection<V>> valuesSupplier;

    public CommonMultiValueMap() {
        this(new HashMap<K, Collection<V>>(), input -> Collections.emptyList());
    }

    public CommonMultiValueMap(@NonNull Supplier<Map<K, Collection<V>>> mapSupplier, @NonNull Function<K, Collection<V>> valuesSupplier) {
        this(mapSupplier.get(), valuesSupplier);
    }

    public CommonMultiValueMap(@NonNull Map<K, Collection<V>> map, @NonNull Function<K, Collection<V>> valuesSupplier) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(valuesSupplier);
        this.targetMap = map;
        this.valuesSupplier = valuesSupplier;
    }

    private Collection<V> getValues(@NonNull K key) {
        Collection<V> values = targetMap.get(key);
        if (values == null) {
            values = valuesSupplier.apply(key);
            targetMap.put(key, values);
        }
        return values;
    }

    @Override
    public V getFirst(K key) {
        return getValue(key, 0);
    }

    @Override
    public V getValue(K key, int index) {
        if (key == null) {
            return null;
        }
        List<V> vs = get(key);
        if (vs.isEmpty()) {
            return null;
        }
        if (index < 0 || index >= vs.size()) {
            return null;
        }
        return vs.get(index);
    }

    @Override
    public void add(K key, V value) {
        if (key == null || value != null) {
            getValues(key).add(value);
        }
    }

    public void addAll(final K key, Collection<? extends V> values) {
        if (key == null) {
            return;
        }
        CollectionUtils.forEach(values, new Consumer<V>() {
            @Override
            public void accept(V v) {
                add(key, v);
            }
        });
    }

    @Override
    public void addAll(MultiValueMap<K, V> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        CollectionUtils.forEach(map, new BiConsumer<K, Collection<V>>() {
            @Override
            public void accept(final K key, Collection<V> values) {
                CollectionUtils.forEach(values, new Consumer<V>() {
                    @Override
                    public void accept(V v) {
                        add(key, v);
                    }
                });
            }
        });
    }

    @Override
    public void addIfAbsent(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        if (!containsKey(key)) {
            add(key, value);
        }
    }

    @Override
    public void removeValue(K key, V value) {
        if (value != null) {
            Collection<V> values = this.getValues(key);
            values.remove(value);
        }
    }


    @Override
    public void set(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        Collection<V> values = valuesSupplier.apply(key);
        values.add(value);
        this.targetMap.put(key, values);
    }

    @Override
    public void setAll(Map<K, V> values) {
        CollectionUtils.forEach(values, this::set);
    }

    @Override
    public Map<K, V> toSingleValueMap() {
        final LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<K, V>(this.targetMap.size());
        CollectionUtils.forEach(this.targetMap, (BiConsumer<K, Collection<V>>) (key, values) -> {
            if (Emptys.isNotEmpty(values)) {
                singleValueMap.put(key, Lists.asList(values).get(0));
            }
        });
        return singleValueMap;
    }

    @Override
    public Map<K, List<V>> toMap() {
        final LinkedHashMap<K, List<V>> map = new LinkedHashMap<K, List<V>>(this.targetMap.size());
        CollectionUtils.forEach(this.targetMap, (BiConsumer<K, Collection<V>>) (key, values) -> {
            if (Emptys.isNotEmpty(values)) {
                map.put(key, Lists.newArrayList(values));
            }
        });
        return map;
    }

    @Override
    public int size() {
        return this.targetMap.size();
    }

    @Override
    public int total() {
        final SimpleIntegerCounter counter = new SimpleIntegerCounter(0);
        CollectionUtils.forEach(this.targetMap, (BiConsumer<K, Collection<V>>) (key, values) -> counter.increment(values.size()));
        return counter.get();
    }

    @Override
    public boolean isEmpty() {
        return total() < 1;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.targetMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return CollectionUtils.anyMatch(this.targetMap, (key, values) -> values.contains(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<V> get(Object key) {
        Collection<V> values = getValues((K) key);
        return Lists.asList(values);
    }

    @Override
    public List<V> remove(Object key) {
        Collection<V> values = this.targetMap.remove(key);
        if (values == null) {
            return null;
        }
        return Lists.asList(values);
    }

    @Override
    public Collection<V> put(final K key, Collection<V> values) {
        Collection<V> old = remove(key);
        addAll(key, values);
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> map) {
        CollectionUtils.forEach(map, new BiConsumer<K, Collection<V>>() {
            @Override
            public void accept(K key, Collection<V> values) {
                put(key, values);
            }
        });
    }

    @Override
    public void clear() {
        CollectionUtils.forEach(this.targetMap, new BiConsumer<K, Collection<V>>() {
            @Override
            public void accept(K key, Collection<V> values) {
                values.clear();
            }
        });
        this.targetMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.targetMap.keySet();
    }

    @Override
    public Collection<Collection<V>> values() {
        return this.targetMap.values();
    }

    @Override
    public Set<Entry<K, Collection<V>>> entrySet() {
        return this.targetMap.entrySet();
    }
}
