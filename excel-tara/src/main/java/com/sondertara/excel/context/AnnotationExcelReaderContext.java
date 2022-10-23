package com.sondertara.excel.context;

import com.sondertara.excel.executor.ExcelReaderExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;
import com.sondertara.excel.meta.model.AnnotationExcelReaderSheetDefinition;
import com.sondertara.excel.meta.model.AnnotationSheet;
import com.sondertara.excel.support.callback.CellReadExCallback;
import com.sondertara.excel.support.callback.RowReadExCallback;
import com.sondertara.excel.support.callback.impl.DefaultCellReadExCallback;
import com.sondertara.excel.support.callback.impl.DefaultRowReadExCallback;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class AnnotationExcelReaderContext<T> implements ExcelRawReaderContext<List<T>> {

    private final Map<Integer, AnnotationSheet> sheetDefinitionMap;
    private final TaraExcelExecutor<List<T>> excelExecutor;
    private final InputStream inputStream;
    private RowReadExCallback rowReadExceptionCallback = new DefaultRowReadExCallback();
    private CellReadExCallback cellReadExceptionCallback = new DefaultCellReadExCallback();

    public AnnotationExcelReaderContext(InputStream is, Class<T> clazz,
                                        RowReadExCallback rowReadExceptionCallback,
                                        CellReadExCallback cellReadExceptionCallback) {
        this.inputStream = is;
        this.sheetDefinitionMap = new HashMap<>();
        if (rowReadExceptionCallback != null) {
            this.rowReadExceptionCallback = rowReadExceptionCallback;
        }
        if (cellReadExceptionCallback != null) {
            this.cellReadExceptionCallback = cellReadExceptionCallback;
        }
        this.excelExecutor = new ExcelReaderExecutor<T>(this);

        AnnotationExcelReaderSheetDefinition<T> annotationExcelReaderSheetDefinition = new AnnotationExcelReaderSheetDefinition<>(
                clazz);

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
    public RowReadExCallback getExcelRowReadExCallback() {
        return this.rowReadExceptionCallback;
    }

    @Override
    public CellReadExCallback getExcelCellReadExCallback() {
        return this.cellReadExceptionCallback;
    }

}
