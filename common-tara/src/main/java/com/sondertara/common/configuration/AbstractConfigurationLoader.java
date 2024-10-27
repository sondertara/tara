package com.sondertara.common.configuration;

import com.sondertara.common.collection.Maps;

import java.util.Map;

public class AbstractConfigurationLoader<T extends Configuration> implements ConfigurationLoader<T> {
    @Override
    public T load(String id) {
        return null;
    }

    @Override
    public Map<String, T> loadAll() {
        return Maps.newHashMap();
    }
}
