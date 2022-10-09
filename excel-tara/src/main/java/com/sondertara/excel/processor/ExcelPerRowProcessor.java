package com.sondertara.excel.processor;

import com.sondertara.excel.meta.model.ExcelRowDef;

/**
 * @author huangxiaohu
 */
public interface ExcelPerRowProcessor {

    void processTotalRow(int totalRows);

    void processPerRow(ExcelRowDef row) throws Exception;
}
