package com.sondertara.excel.resolver.builder;

import com.sondertara.excel.function.ExportFunction;

/**
 * @author huangxiaohu
 */
public class DateQueryBuilder<T> {

    private final AbstractExcelWriter<T> excelWriter;

    public DateQueryBuilder(AbstractExcelWriter<T> excelWriter) {
        this.excelWriter = excelWriter;
    }

    public <R> DateQueryBuilder<T> mapper(Class<R> excelClass, ExportFunction<R> query) {
        this.excelWriter.getWriterContext().addMapper(excelClass, query);
        return this;
    }

    public AbstractExcelWriter<T> then() {
        return this.excelWriter;
    }
}
