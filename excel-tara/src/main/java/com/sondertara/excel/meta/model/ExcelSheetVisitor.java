package com.sondertara.excel.meta.model;

import com.sondertara.excel.base.TaraExcelConfig;

/**
 * Visitor the excel definition
 * @author huangxiaohu
 */
public interface ExcelSheetVisitor {

    /**
     * first data row number(base is 0)
     *
     * @return the row number
     */
    int firstDataRow();

    /**
     * max data row count for per sheet
     *
     * @return
     */
    default int maxRowsPerSheet() {
        return TaraExcelConfig.CONFIG.getDefaultRowPerSheet();
    }
}
