package com.sondertara.excel.resolver.builder;

import com.sondertara.excel.function.ExportFunction;

import java.util.List;

/**
 * @author huangxiaohu
 */
public class DateQueryBuilder<T extends AbstractExcelWriter<?>, R> {

    private final T excelWriter;
    private final Class<R> excelClass;

    public DateQueryBuilder(T excelWriter, Class<R> excelClass) {
        this.excelWriter = excelWriter;
        this.excelClass = excelClass;
    }

    public DateQueryBuilder<T, R> addData(ExportFunction<R> query) {
        this.excelWriter.getWriterContext().addData(excelClass, query);
        return this;
    }

    public DateQueryBuilder<T, R> addData(List<R> dataList) {
        this.excelWriter.getWriterContext().addData(dataList);
        return this;
    }

    public T then() {
        return this.excelWriter;
    }
}
