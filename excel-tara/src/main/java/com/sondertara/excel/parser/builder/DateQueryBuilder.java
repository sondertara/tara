package com.sondertara.excel.parser.builder;

import com.sondertara.excel.boot.AbstractExcelWriter;
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

    public AbstractExcelWriter<T> pagination(Integer start, Integer end, Integer pageSize) {
        this.writerHandler.pagination(start, end, pageSize);

        return this.writerHandler;
    }

    public ExcelMappingBuilder<T> mapper(Class<?> excelClass) {
        return new ExcelMappingBuilder<>(excelClass, this);
    }

    public AbstractExcelWriter<T> getWriterHandler() {
        return writerHandler;
    }

    public DateQueryBuilder<T> excelMapping(Class<?> excelClass, ExportFunction<?> query) {
        writerHandler.excelMapping(excelClass, query);
        return this;
    }
}
