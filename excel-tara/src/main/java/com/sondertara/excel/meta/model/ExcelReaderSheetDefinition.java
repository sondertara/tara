package com.sondertara.excel.meta.model;

public interface ExcelReaderSheetDefinition<T> extends ExcelSheetDefinition {

    int getSheetIndex();

    int[] getSheetIndexs();

}
