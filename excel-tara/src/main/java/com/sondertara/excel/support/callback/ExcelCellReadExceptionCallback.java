package com.sondertara.excel.support.callback;


import com.sondertara.excel.meta.model.ExcelCellDefinition;
import com.sondertara.excel.meta.model.ExcelRowDefinition;

/**
 *
 * @author chenzw
 * @since 1.0.3
 */
public interface ExcelCellReadExceptionCallback {

    void call(ExcelRowDefinition rowDefinition, ExcelCellDefinition cellDefinition, Exception ex);
}
