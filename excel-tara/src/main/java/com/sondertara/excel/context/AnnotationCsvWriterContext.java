package com.sondertara.excel.context;

import com.sondertara.excel.executor.ExcelCsvWriterExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;

public class AnnotationCsvWriterContext extends BaseAnnotationExcelWriterContext<String> {


    @Override
    public TaraExcelExecutor<String> getExecutor() {
        return new ExcelCsvWriterExecutor(this.getSheetDefinitions());
    }
}
