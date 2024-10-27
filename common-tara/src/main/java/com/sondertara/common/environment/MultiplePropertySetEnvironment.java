package com.sondertara.common.environment;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.propertyset.MultiplePropertySet;
import com.sondertara.common.propertyset.PropertySet;

import java.util.List;

/**
 *  */
public class MultiplePropertySetEnvironment implements Environment {
    private MultiplePropertySet propertySet;

    public MultiplePropertySetEnvironment(String name, List<PropertySet> propertySets) {
       this(new MultiplePropertySet(name, propertySets));
    }

    public MultiplePropertySetEnvironment(MultiplePropertySet propertySet) {
        this.propertySet=propertySet;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key,null);
    }

    @Override
    public String getProperty(String key, String valueIfAbsent) {
        Object value = this.propertySet.getProperty(key);
        value = ObjectUtils.defaultIfNull(value, valueIfAbsent);
        return ObjectUtils.toString(value);
    }
}
