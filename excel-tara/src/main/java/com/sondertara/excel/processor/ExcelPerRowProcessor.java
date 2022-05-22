package com.sondertara.excel.processor;


import com.sondertara.excel.meta.model.ExcelRowDefinition;

/**
 * @author chenzw
 */
public interface ExcelPerRowProcessor {

    void processTotalRow(int totalRows);

    void processPerRow(ExcelRowDefinition row) throws Exception;
}
