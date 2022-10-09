package com.sondertara.excel.parser.builder;

import com.sondertara.excel.function.ExportFunction;

/**
 * @author huangxiaohu
 */
public class ExcelMappingBuilder<T> {
    private final Class<?> excelClass;

    private final DateQueryBuilder<T> dateQueryBuilder;

    public ExcelMappingBuilder(Class<?> excelClass, DateQueryBuilder<T> dateQueryBuilder) {
        this.excelClass = excelClass;
        this.dateQueryBuilder = dateQueryBuilder;
    }

    public <R> DateQueryBuilder<T> query(ExportFunction<R> query) {
        dateQueryBuilder.excelMapping(excelClass, query);
        return this.dateQueryBuilder;
    }

}
