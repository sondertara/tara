package com.sondertara.excel.meta.support;

import com.sondertara.excel.entity.ExcelWriteSheetEntity;

/**
 * @author huangxiaohu
 */
public class DefaultExcelColumnProvider implements ExcelColumnProvider {

    protected final ExcelWriteSheetEntity defaultSheetEntity;

    public DefaultExcelColumnProvider() {
        this.defaultSheetEntity = ExcelWriteSheetEntity.builder().build();
    }

    @Override
    public ExcelWriteSheetEntity resolve(Class<?> clazz) {
        return ExcelMappingFactory.resolveSheetEntity(clazz);
    }
}
