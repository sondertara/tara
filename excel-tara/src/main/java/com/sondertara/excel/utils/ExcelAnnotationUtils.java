package com.sondertara.excel.utils;

import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.meta.annotation.ExcelExportField;

/**
 * @author huangxiaohu
 */
public class ExcelAnnotationUtils {

    public static String getColName(ExcelExportField excelExportField) {
        if (StringUtils.isNotBlank(excelExportField.value())) {
            return excelExportField.value().trim();
        }

        return excelExportField.colName().trim();
    }
}
