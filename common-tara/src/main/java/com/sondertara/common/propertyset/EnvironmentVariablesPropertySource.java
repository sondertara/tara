package com.sondertara.common.propertyset;

/**
 *  */
public class EnvironmentVariablesPropertySource extends MapPropertySet {
    public EnvironmentVariablesPropertySource() {
        super("envVars");
    }

    public EnvironmentVariablesPropertySource(String name) {
        super(name, System.getenv());
    }

}
