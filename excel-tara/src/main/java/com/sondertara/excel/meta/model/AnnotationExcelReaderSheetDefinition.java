package com.sondertara.excel.meta.model;

import com.sondertara.common.util.StringFormatter;
import com.sondertara.common.util.StringUtils;
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
        this.bindType = excelImport.bindType();
    }

    private void initColumnFields() {
        Field[] fields = this.mappingClass.getDeclaredFields();
        int colIndex = 0;
        for (Field field : fields) {
            ExcelImportField importColumn = field.getAnnotation(ExcelImportField.class);
            if (importColumn != null) {
                String title = importColumn.title();
                int index = 0;
                field.setAccessible(true);
                switch (this.bindType) {
                    case ORDER:
                        colIndex += 1;
                        index = colIndex;
                        break;
                    case COL_INDEX:
                        index = importColumn.colIndex();
                        break;
                    case TITLE:
                        //Set the tmp index,when parse the Excel file will reset the relation.
                        colIndex+=1;
                        index = colIndex;
                        if (StringUtils.isBlank(title)) {
                            throw new ExcelReaderException(StringFormatter.format("Excel bind by title,the title of field[{}] must be not empty", field.getName()));
                        }
                        break;
                    default:
                }
                this.getColFields().put(index, field);
                this.getTitles().put(index, title);
            }
        }
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return this.mappingClass.getAnnotation(clazz);
    }

}
