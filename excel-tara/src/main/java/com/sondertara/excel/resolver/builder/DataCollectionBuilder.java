package com.sondertara.excel.resolver.builder;

import java.util.List;

/**
 * @author huangxiaohu
 */
public class DataCollectionBuilder<T> {

    private final AbstractExcelWriter<T> excelWriter;

    public DataCollectionBuilder(AbstractExcelWriter<T> excelWriter) {
        this.excelWriter = excelWriter;
    }

    public DataCollectionBuilder<T> addData(List<?> data) {
        this.excelWriter.getWriterContext().addData(data);
        return this;
    }

    public AbstractExcelWriter<T> then() {
        return this.excelWriter;
    }
}
