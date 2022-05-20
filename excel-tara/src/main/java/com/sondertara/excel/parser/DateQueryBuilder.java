package com.sondertara.excel.parser;

import com.sondertara.excel.function.ExportFunction;

/**
 * @author huangxiaohu
 */
public class DateQueryBuilder {

    private final AbstractExcelWriter writerHandler;


    public DateQueryBuilder(AbstractExcelWriter writerHandler) {
        this.writerHandler = writerHandler;
    }

    public DateQueryBuilder pagination(Integer start, Integer end, Integer pageSize) {
        this.writerHandler.pagination(start, end, pageSize);

        return this;
    }

    public <R> AbstractExcelWriter query(ExportFunction<R> exportFunction) {
        this.writerHandler.exportFunction(exportFunction);
        return this.writerHandler;
    }


}
