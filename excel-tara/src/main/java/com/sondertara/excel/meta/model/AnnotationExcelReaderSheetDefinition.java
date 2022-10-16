package com.sondertara.excel.meta.model;

import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.meta.annotation.ExcelImport;
import com.sondertara.excel.meta.annotation.ExcelImportField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author huangxiaohu
 */
public class AnnotationExcelReaderSheetDefinition<T> extends AnnotationSheet {

    private int[] sheetIndexes;

    public int[] getSheetIndexes() {
        return sheetIndexes;
    }

    public AnnotationExcelReaderSheetDefinition(Class<T> clazz) {
        super(clazz);
        init();
    }

    private void init() {
        initSheetMeta();
        initColumnFields();
    }

    private void initSheetMeta() {
        ExcelImport excelImport = this.mappingClass.getAnnotation(ExcelImport.class);
        if (excelImport == null) {
            throw new ExcelReaderException("Class[" + this.mappingClass.getCanonicalName() + "] miss @ExcelImport!");
        }
        this.sheetIndexes = excelImport.sheetIndex();
        this.firstDataRow = excelImport.firstDataRow();
        this.enableColIndex = excelImport.enableColIndex();
    }

    private void initColumnFields() {
        Field[] fields = this.mappingClass.getDeclaredFields();
        int colIndex = 1;
        for (Field field : fields) {
            ExcelImportField importColumn = field.getAnnotation(ExcelImportField.class);
            if (importColumn != null) {
                field.setAccessible(true);
                this.getColFields().put(enableColIndex ? importColumn.colIndex() : colIndex++, field);
                this.getTitles().put(importColumn.colIndex(), importColumn.title());
            }
        }
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return this.mappingClass.getAnnotation(clazz);
    }

}
