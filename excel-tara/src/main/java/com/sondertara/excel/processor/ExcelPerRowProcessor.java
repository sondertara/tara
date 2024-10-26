package com.sondertara.excel.processor;

import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.meta.model.ExcelSheetDef;

/**
 * @author huangxiaohu
 */
public interface ExcelPerRowProcessor {

    void processSheet(ExcelSheetDef curExcelSheet);

    void processTotalRow(int totalRows);

    void processPerRow(ExcelRowDef row) throws Exception;
}
