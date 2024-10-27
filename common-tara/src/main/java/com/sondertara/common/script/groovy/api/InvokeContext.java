package com.sondertara.common.script.groovy.api;

/**
 * Created by luyi on 16/5/16.
 */
public interface InvokeContext {

    <T> T get(String key);

}
