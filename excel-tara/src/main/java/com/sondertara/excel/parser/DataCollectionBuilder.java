package com.sondertara.excel.parser;

import java.util.Collection;

/**
 * @author huangxiaohu
 */
public class DataCollectionBuilder {

    private final AbstractExcelWriter excelWriter;

    public DataCollectionBuilder(AbstractExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
    }

    public AbstractExcelWriter rows(Collection<?> list) {
        return this.excelWriter;
    }
}
