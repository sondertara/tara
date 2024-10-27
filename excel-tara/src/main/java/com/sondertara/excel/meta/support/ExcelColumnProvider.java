package com.sondertara.excel.meta.support;

import com.sondertara.excel.entity.ExcelWriteSheetEntity;

/**
 * @author huangxiaohu
 */
public interface ExcelColumnProvider {

    ExcelWriteSheetEntity resolve(Class<?> clazz);
}
