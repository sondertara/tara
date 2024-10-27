package com.sondertara.common.script.groovy;

import com.sondertara.common.script.groovy.api.InvokeContext;

import java.util.Map;

/**
 * Created by luyi on 16/5/16.
 */
public class DefaultInvokeContext implements InvokeContext {
    private static final ThreadLocal<Map<String, Object>> PARAMS = new ThreadLocal<Map<String, Object>>();

    @Override
    public <T> T get(String key) {
        Map<String, Object> map = PARAMS.get();
        if (map != null) {
            return (T) map.get(key);
        }
        return null;
    }

    public void setParams(Map<String, Object> params) {
        PARAMS.set(params);
    }

    public void clear() {
        PARAMS.set(null);
    }
}
