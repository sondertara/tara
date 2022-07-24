package com.sondertara.excel.parser.builder;

import com.sondertara.excel.enums.ExcelDataType;
import com.sondertara.excel.parser.AbstractExcelWriter;

import java.util.List;

/**
 * @author huangxiaohu
 */
public class DataCollectionBuilder<T> {

    private final AbstractExcelWriter<T> excelWriter;

    public DataCollectionBuilder(AbstractExcelWriter<T> excelWriter) {
        this.excelWriter = excelWriter;
        this.excelWriter.setExcelDataType(ExcelDataType.DIRECT);
    }

    public AbstractExcelWriter<T> addData(List<?>... data) {
        this.excelWriter.getWriterContext().addData(data);
        return this.excelWriter;
    }
}
