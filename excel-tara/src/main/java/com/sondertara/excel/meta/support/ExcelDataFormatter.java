package com.sondertara.excel.meta.support;

public interface ExcelDataFormatter {
    /**
     * format cell value
     *
     * @param input
     * @return
     */
    Object format(Object input);
}
