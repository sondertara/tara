package com.sondertara.common.propertyset;


import com.sondertara.common.collection.Maps;

/**
 *  */
public class SystemPropertiesPropertySource extends MapPropertySet {
    public SystemPropertiesPropertySource() {
        this("systemProperties");
    }
    public SystemPropertiesPropertySource(String name) {
        super(name, Maps.newStringMap(System.getProperties()));
    }

}
