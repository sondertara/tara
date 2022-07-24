package com.sondertara.excel.context;


import com.sondertara.excel.executor.ExcelReaderExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;
import com.sondertara.excel.meta.AnnotationSheet;
import com.sondertara.excel.meta.model.AnnotationExcelReaderSheetDefinition;
import com.sondertara.excel.support.callback.ExcelCellReadExceptionCallback;
import com.sondertara.excel.support.callback.ExcelRowReadExceptionCallback;
import com.sondertara.excel.support.callback.impl.DefaultExcelCellReadExceptionCallback;
import com.sondertara.excel.support.callback.impl.DefaultExcelRowReadExceptionCallback;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenzw
 */
public class AnnotationExcelReaderContext<T> implements ExcelReaderContext {

    private final Map<Integer, AnnotationSheet> sheetDefinitionMap;
    private final TaraExcelExecutor<List<T>> excelExecutor;
    private final InputStream inputStream;
    private ExcelRowReadExceptionCallback rowReadExceptionCallback = new DefaultExcelRowReadExceptionCallback();
    private ExcelCellReadExceptionCallback cellReadExceptionCallback = new DefaultExcelCellReadExceptionCallback();


    public AnnotationExcelReaderContext(InputStream is, Class<T> clazz, ExcelRowReadExceptionCallback rowReadExceptionCallback, ExcelCellReadExceptionCallback cellReadExceptionCallback) {
        this.inputStream = is;
        this.sheetDefinitionMap = new HashMap<>();
        if (rowReadExceptionCallback != null) {
            this.rowReadExceptionCallback = rowReadExceptionCallback;
        }
        if (cellReadExceptionCallback != null) {
            this.cellReadExceptionCallback = cellReadExceptionCallback;
        }
        this.excelExecutor = new ExcelReaderExecutor<T>(this);

        AnnotationExcelReaderSheetDefinition<T> annotationExcelReaderSheetDefinition = new AnnotationExcelReaderSheetDefinition<>(clazz);

        int[] sheetIndexes = annotationExcelReaderSheetDefinition.getSheetIndexes();
        for (int sheetIndex : sheetIndexes) {
            this.sheetDefinitionMap.put(sheetIndex, annotationExcelReaderSheetDefinition);
        }

    }

    @Override
    public Map<Integer, AnnotationSheet> getSheetDefinitions() {
        return this.sheetDefinitionMap;
    }

    @Override
    public TaraExcelExecutor<List<T>> getExecutor() {
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
