package com.sondertara.excel.utils;

import com.sondertara.excel.meta.annotation.ExcelExportField;

/**
 * @author huangxiaohu
 */
public class ExcelAnnotationUtils {

    public static String[] getColName(ExcelExportField excelExportField) {

        return excelExportField.colName();
    }
}
