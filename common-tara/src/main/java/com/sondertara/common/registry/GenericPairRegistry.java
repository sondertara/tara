package com.sondertara.common.registry;

import com.sondertara.common.struct.Pair;
import com.sondertara.common.collection.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenericPairRegistry<K, V, P extends Pair<K, V>> extends AbstractRegistry<K, P> {

    public GenericPairRegistry() {
        this(new ConcurrentHashMap<K, P>());
    }

    public GenericPairRegistry(Map<K, P> registry) {
        super(registry);
    }

    @Override
    public void register(P pair) {
        this.registry.put(pair.getKey(), pair);
    }

    public List<K> keys() {
        return Lists.newArrayList(registry.keySet());
    }

    public List<P> pairs() {
        return Lists.newArrayList(registry.values());
    }

}
