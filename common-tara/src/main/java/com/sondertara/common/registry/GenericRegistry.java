package com.sondertara.common.registry;


import com.sondertara.common.base.Named;
import com.sondertara.common.collection.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenericRegistry<T extends Named> extends AbstractRegistry<String, T> {

    public GenericRegistry() {
        this(new ConcurrentHashMap<String, T>());
    }

    public GenericRegistry(Map<String, T> registry) {
        this.registry = registry;
    }

    @Override
    public void register(T t) {
        this.registry.put(t.getName(), t);
    }

    public List<String> names() {
        return Lists.newArrayList(registry.keySet());
    }

    public List<T> instances(){
        return Lists.newArrayList(registry.values());
    }

}
