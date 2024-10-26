package com.sondertara.excel.context;

import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.executor.ExcelWriterExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author huangxiaohu
 */
public class AnnotationExcelWriterContext extends BaseAnnotationExcelWriterContext<SXSSFWorkbook> {

    private final SXSSFWorkbook sxssfWorkbook;

    private volatile boolean executed = false;

    public AnnotationExcelWriterContext() {
        this.sxssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(), Constants.DEFAULT_ROW_ACCESS_WINDOW_SIZE);
    }

    @Override
    public SXSSFWorkbook getResult() {
        if (!executed) {
            getExecutor().execute();
            executed = true;
        }
        return this.sxssfWorkbook;
    }

    @Override
    public TaraExcelExecutor getExecutor() {
        return new ExcelWriterExecutor(sxssfWorkbook, this);
    }
}
