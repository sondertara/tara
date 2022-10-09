package com.sondertara.excel.executor;

import com.sondertara.common.exception.TaraException;
import com.sondertara.excel.exception.ExcelWriterException;
import com.sondertara.excel.meta.style.CellStyleBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * 单元格样式缓存
 * 
 * @author huangxiaohu
 */
public class CellStyleCache {

    private volatile static CellStyleCache instance = null;
    private final Map<String, CellStyleBuilder> cellStyleCacheMap;

    public static CellStyleCache getInstance() {
        if (instance == null) {
            synchronized (CellStyleCache.class) {
                if (instance == null) {
                    return new CellStyleCache();
                }
            }
        }
        return instance;
    }

    private CellStyleCache() {
        this.cellStyleCacheMap = new HashMap<>();
    }

    private void addCache(String className, final CellStyleBuilder cellStyleBuilder) {
        this.cellStyleCacheMap.put(className, cellStyleBuilder);
    }

    public CellStyleBuilder getCellStyleInstance(final Class<?> clazz) {

        return this.cellStyleCacheMap.computeIfAbsent(clazz.getName(), key -> {
            try {
                if (CellStyleBuilder.class.isAssignableFrom(clazz)) {
                    return (CellStyleBuilder) clazz.newInstance();
                } else {
                    throw new ExcelWriterException(
                            "CellStyle [" + clazz + "] not assignable from CellStyleBuilder.class");
                }
            } catch (final Exception e) {
                throw new TaraException("Get CellBuilder error", e);
            }
        });
    }

    public void removeCache(String className) {
        this.cellStyleCacheMap.remove(className);
    }

}