package com.sondertara.common.script.groovy.management;

import com.sondertara.common.script.groovy.AbstractScriptEngine;
import com.sondertara.common.text.StringUtils;
import groovy.lang.GroovySystem;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luyi on 16/4/19.
 */
public abstract class DefaultScriptManager extends AbstractScriptEngine<Script> implements
        ScriptManager {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultScriptManager.class);

    //[scriptId--instance]
    private final ConcurrentHashMap<String, Script> instanceCache = new ConcurrentHashMap<String, Script>();

    public DefaultScriptManager() {
    }

    @Override
    protected Script getScriptInstance(String scriptId) {
        return instanceCache.get(scriptId);
    }

    @Override
    public void registerScript(String scriptId, String scriptText) {
        assertTure(StringUtils.isNotBlank(scriptId), "scriptId is empty!");
        assertTure(StringUtils.isNotBlank(scriptText), "scriptText is empty!");
        removeScriptId(scriptId);
        instanceCache.put(scriptId, generateScriptInstance(scriptId, scriptText));
    }

    @Override
    public void deRegisterScript(String scriptId) {
        removeScriptId(scriptId);
    }

    private void removeScriptId(String scriptId) {
        Object old = instanceCache.remove(scriptId);
        if (old == null) {
            return;
        }
        clearAllClassInfo(old.getClass());
    }

    @Override
    public void removeAllScript() {
        for (Map.Entry entry : instanceCache.entrySet()) {
            clearAllClassInfo(entry.getValue().getClass());
        }
        instanceCache.clear();
    }

    @Override
    public String getScriptState() {
        StringBuilder sb = new StringBuilder("scriptIds: ");
        for (String id : instanceCache.keySet()) {
            sb.append(id).append(File.separatorChar);
        }
        String stat = sb.toString();
        logger.info(stat);
        return stat;
    }

    @Override
    public boolean isEmpty() {
        return instanceCache.isEmpty();
    }

    public void assertTure(boolean flag, String errMsg) {
        if (!flag) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    /**
     * ===================================================================
     */

    /**
     * 清除引用,方便类卸载的方法;
     */
    public void clearAllClassInfo(Class<?> type) {
        try {
            GroovySystem.getMetaClassRegistry().removeMetaClass(type);
        } catch (Throwable e) {
            logger.warn("clear groovy class cache fail!" + e.getMessage());
        }
    }
}
