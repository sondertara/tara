package com.sondertara.common.script.groovy;

/**
 * @author luyi on 16/4/19.
 */
public abstract class AbstractScriptEngine<T> {

    /**
     * 根据脚本加工为脚本实例
     *
     * @param scriptText
     * @return
     */
    protected abstract T generateScriptInstance(String scriptName, String scriptText);

    /***
     * 根据脚本Id 获取脚本实例
     *
     * @param scriptId 脚本唯一标识
     * @return
     */
    protected abstract T getScriptInstance(String scriptId);

}
