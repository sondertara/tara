package com.sondertara.common.propertyset;


import com.sondertara.common.base.Assert;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.accessor.BasedStringAccessor;

public class PropertySetAccessor extends BasedStringAccessor<String, PropertySet> {
    @Override
    public Object get(String key) {
        return this.getString(key, (String) null);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Assert.notEmpty(key, "the property name is null or empty");
        PropertySet propertySource = getTarget();
        if (propertySource.containsProperty(key)) {
            return ObjectUtils.toString(propertySource.getProperty(key));
        }
        return defaultValue;
    }

    @Override
    public void set(String key, Object value) {
        // ignore it
    }

    @Override
    public void remove(String key) {
        // ignore it
    }

}
