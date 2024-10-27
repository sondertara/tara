package com.sondertara.common.script.groovy.api;

import java.util.Map;

/**
 * @author: kevin.luy@antfin.com
 * @create: 2020-04-17 16:38
 **/
public interface ScriptInvokeInterceptor {

    /**
     * 执行前置拦截器
     *
     * @param scriptName
     * @param scriptInstance
     * @param scriptInvocationParams
     * @throws Exception
     */
    void preHandle(String scriptName, Object scriptInstance, Map<String, Object> scriptInvocationParams)
        throws Exception;
}
