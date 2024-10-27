package com.sondertara.common.environment;

public interface Environment {
    String getProperty(String key);

    String getProperty(String key, String valueIfAbsent);

}
