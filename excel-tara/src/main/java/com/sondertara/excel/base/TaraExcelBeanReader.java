package com.sondertara.excel.base;

import com.sondertara.excel.meta.annotation.ExcelImport;

import java.util.List;

/**
 * @author huangxiaohu
 */
public interface TaraExcelBeanReader {
    /**
     * reade excel to bean
     *
     * @param clazz the Excel class {@link ExcelImport}
     * @param <T>   type
     * @return list
     */
    <T> List<T> read(Class<T> clazz);

}
