package com.sondertara.excel.parser.builder;

import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.function.ExportFunction;

/**
 * @author huangxiaohu
 */
public class DateQueryBuilder<T> {

    private final AbstractExcelWriter<T> writerHandler;

    public DateQueryBuilder(AbstractExcelWriter<T> writerHandler) {
        this.writerHandler = writerHandler;
        this.writerHandler.setExcelDataType(ExcelDataType.QUERY);
    }

    public <R> AbstractExcelWriter<T> mapper(Class<?> excelClass, ExportFunction<R> query) {
        this.writerHandler.excelMapping(excelClass, query);
        return this.writerHandler;
    }
}
