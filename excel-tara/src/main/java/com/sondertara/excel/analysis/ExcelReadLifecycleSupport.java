package com.sondertara.excel.analysis;

/**
 * @author huangxiaohu
 */
public interface ExcelReadLifecycleSupport {
    /**
     * before parse sheet
     * @param sheetIndex sheet index
     */
    void beforeParseSheet(int sheetIndex);

    /**
     * after parse sheet
     * @param sheetIndex sheet index
     */
    void afterParseSheet(int sheetIndex);

    /**
     * finish parse sheet
     */
    void finish();
}
