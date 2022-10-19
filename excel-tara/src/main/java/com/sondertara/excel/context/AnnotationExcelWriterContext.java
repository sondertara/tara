package com.sondertara.excel.context;

import com.sondertara.excel.executor.ExcelWriterExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;
import org.apache.poi.ss.usermodel.Workbook;

public class AnnotationExcelWriterContext extends BaseAnnotationExcelWriterContext<Workbook> {
    @Override
    public TaraExcelExecutor<Workbook> getExecutor() {
        return new ExcelWriterExecutor(this);
    }
}
