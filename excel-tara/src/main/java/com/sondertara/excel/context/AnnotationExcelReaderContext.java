package com.sondertara.excel.context;


import com.sondertara.excel.executor.ExcelExecutor;
import com.sondertara.excel.executor.ExcelReaderExecutor;
import com.sondertara.excel.meta.model.AnnotationExcelReaderSheetDefinition;
import com.sondertara.excel.meta.model.ExcelReaderSheetDefinition;
import com.sondertara.excel.meta.model.ExcelSheetDefinition;
import com.sondertara.excel.support.callback.ExcelCellReadExceptionCallback;
import com.sondertara.excel.support.callback.ExcelRowReadExceptionCallback;
import com.sondertara.excel.support.callback.impl.DefaultExcelCellReadExceptionCallback;
import com.sondertara.excel.support.callback.impl.DefaultExcelRowReadExceptionCallback;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenzw
 */
public class AnnotationExcelReaderContext implements ExcelReaderContext {

    private Map<Integer, ExcelSheetDefinition> sheetdefinitions;
    private ExcelExecutor excelExecutor;
    private InputStream inputStream;
    private ExcelRowReadExceptionCallback rowReadExceptionCallback = new DefaultExcelRowReadExceptionCallback();
    private ExcelCellReadExceptionCallback cellReadExceptionCallback = new DefaultExcelCellReadExceptionCallback();


    public <T> AnnotationExcelReaderContext(InputStream is, Class<T> clazz, ExcelRowReadExceptionCallback rowReadExceptionCallback, ExcelCellReadExceptionCallback cellReadExceptionCallback) {
        this.inputStream = is;
        this.sheetdefinitions = new HashMap<>();
        if (rowReadExceptionCallback != null) {
            this.rowReadExceptionCallback = rowReadExceptionCallback;
        }
        if (cellReadExceptionCallback != null) {
            this.cellReadExceptionCallback = cellReadExceptionCallback;
        }
        this.excelExecutor = new ExcelReaderExecutor<T>(this);

        ExcelReaderSheetDefinition sheetDefinition = new AnnotationExcelReaderSheetDefinition(clazz);
        int[] sheetIndexs = sheetDefinition.getSheetIndexs();
        for (int sheetIndex : sheetIndexs) {
            this.sheetdefinitions.put(sheetIndex, sheetDefinition);
        }
    }

    @Override
    public Map<Integer, ExcelSheetDefinition> getSheetDefinitions() {
        return this.sheetdefinitions;
    }

    @Override
    public ExcelExecutor getExecutor() {
        return this.excelExecutor;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public ExcelRowReadExceptionCallback getExcelRowReadExceptionCallback() {
        return this.rowReadExceptionCallback;
    }

    @Override
    public ExcelCellReadExceptionCallback getExcelCellReadExceptionCallback() {
        return this.cellReadExceptionCallback;
    }


}
