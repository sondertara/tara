
package com.sondertara.common.script.groovy;

import com.sondertara.common.script.groovy.api.ScriptInvokeInterceptor;
import com.sondertara.common.script.groovy.sandbox.GroovyInterceptor;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.Map;

/**
 * @author luyi on 16/4/19.
 */
public interface ScriptEngine {

    /**
     * 执行入口
     *
     * @param scriptName   脚本Id,
     * @param scriptParams 脚本执行的参数[参数名--参数实例]
     * @param <T>          可以为null
     * @return
     */
    <T> T invoke(String scriptName, Map<String, Object> scriptParams);

    void setCompilerConfiguration(CompilerConfiguration cc);

    void addGroovyInterceptor(GroovyInterceptor groovyInterceptor);

    void addScriptInvokeInterceptor(ScriptInvokeInterceptor scriptInvokeInterceptor);
}
