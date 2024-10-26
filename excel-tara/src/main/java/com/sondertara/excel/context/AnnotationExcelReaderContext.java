package com.sondertara.excel.context;

import com.sondertara.excel.executor.ExcelReaderExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;
import com.sondertara.excel.lifecycle.ExcelReadListener;
import com.sondertara.excel.meta.model.AnnotationExcelReaderSheetDefinition;
import com.sondertara.excel.meta.model.AnnotationSheet;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class AnnotationExcelReaderContext<T> implements ExcelRawReaderContext<T> {

    private final Map<Integer, AnnotationSheet> sheetDefinitionMap;
    private final TaraExcelExecutor excelExecutor;
    private final InputStream inputStream;

    private final ExcelReadListener<T> readListener;





    public AnnotationExcelReaderContext(InputStream is, Class<T> clazz, ExcelReadListener<T> excelReadListener) {
        this.inputStream = is;
        this.sheetDefinitionMap = new HashMap<>();

        this.readListener=excelReadListener;

        this.excelExecutor = new ExcelReaderExecutor<>(this);

        AnnotationExcelReaderSheetDefinition<T> annotationExcelReaderSheetDefinition = new AnnotationExcelReaderSheetDefinition<>(
                clazz);


        int[] sheetIndexes = annotationExcelReaderSheetDefinition.getSheetIndexes();
        for (int sheetIndex : sheetIndexes) {
            this.sheetDefinitionMap.put(sheetIndex, annotationExcelReaderSheetDefinition);
        }

    }


    @Override
    public ExcelReadListener<T> getReadListener() {
        return this.readListener;
    }

    @Override
    public Map<Integer, AnnotationSheet> getSheetDefinitions() {
        return this.sheetDefinitionMap;
    }

    @Override
    public TaraExcelExecutor getExecutor() {
        return this.excelExecutor;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }
}
