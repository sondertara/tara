package com.sondertara.common.text.properties;

import com.sondertara.common.collection.Maps;
import com.sondertara.common.propertyset.MapPropertySet;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Deprecated
public class PropertiesPropertySource extends MapPropertySet {


    public PropertiesPropertySource(String name, Properties properties) {
        super(name, Maps.newStringMap(properties));
    }

    public PropertiesPropertySource(Properties properties) {
        this("unknown", properties);
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("1",1);
        properties.put("2",1);
        properties.put(3,1);
        Map<String, Object> map = Maps.map(properties, Object::toString, Function.identity());
        System.out.println(map);
    }

}
