
package com.sondertara.common.script.groovy.management;

/**
 * @author luyi on 16/4/19.
 */
public interface ScriptManager {

    /**
     * 登记
     *
     * @param scriptId   不能为null,属性不能为空,否则参数校验异常
     * @param scriptText 不能为空,否则参数校验异常
     */
    void registerScript(String scriptId, String scriptText);

    /**
     * 注销
     *
     * @param scriptId
     */
    void deRegisterScript(String scriptId);

    /**
     * 强制销毁所有脚本
     */
    void removeAllScript();

    String getScriptState();

    /**
     * 在管理的脚本实例是否为空
     *
     * @return
     */
    boolean isEmpty();
}
