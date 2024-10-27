package com.sondertara.common.text.placeholder;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.propertyset.PropertySet;

import java.util.Objects;

public class PropertySetPlaceholderParser implements PlaceholderParser {
    private PropertySet propertySource;

    public PropertySetPlaceholderParser() {

    }

    public PropertySetPlaceholderParser(PropertySet propertySource) {
        setPropertySet(propertySource);
    }

    public PropertySet getPropertySet() {
        return propertySource;
    }

    public void setPropertySet(PropertySet propertySource) {
        Objects.requireNonNull(propertySource);
        this.propertySource = propertySource;
    }

    @Override
    public String parse(String variable) {
        return ObjectUtils.toString(propertySource.getProperty(variable));
    }
}
