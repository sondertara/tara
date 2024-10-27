package com.sondertara.common.environment;

import com.sondertara.common.accessor.BasedStringAccessor;

public class EnvironmentAccessor extends BasedStringAccessor<String, Environment> {

    @Override
    public Object get(String key) {
        return getTarget().getProperty(key);
    }

    @Override
    public boolean has(String key) {
        return getTarget().getProperty(key) != null;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getTarget().getProperty(key, defaultValue);
    }

    @Override
    public void set(String key, Object value) {

    }

    @Override
    public void remove(String key) {

    }
}
