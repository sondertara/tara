package com.sondertara.common.text.properties;

import com.sondertara.common.text.placeholder.PropertySetPlaceholderParser;

import java.util.Properties;

public class PropertiesPlaceholderParser extends PropertySetPlaceholderParser {

    public PropertiesPlaceholderParser(Properties properties) {
        super(new PropertiesPropertySource(properties));
    }
}
