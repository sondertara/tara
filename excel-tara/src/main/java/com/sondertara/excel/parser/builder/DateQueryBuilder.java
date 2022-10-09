package com.sondertara.excel.parser.builder;

import com.sondertara.excel.entity.PageQueryParam;
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


        this.writerHandler.setPageQueryParam(PageQueryParam.builder().pageEnd(end).pageStart(start).pageSize(pageSize).build());
        return this.writerHandler;
    }

    public AbstractExcelWriter<T> then() {
        return this.writerHandler;
    }

    public ExcelMappingBuilder<T> mapper(Class<?> excelClass) {
        return new ExcelMappingBuilder<>(excelClass, this);
    }

    protected AbstractExcelWriter<T> getWriterHandler() {
        return writerHandler;
    }

    protected DateQueryBuilder<T> excelMapping(Class<?> excelClass, ExportFunction<?> query) {
        writerHandler.excelMapping(excelClass, query);
        return this;
    }
}
