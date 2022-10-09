package com.sondertara.excel.support.callback;

import com.sondertara.excel.meta.model.ExcelCellDef;
import com.sondertara.excel.meta.model.ExcelRowDef;

/**
 * @author huangxiaohu
 * @since 1.0.3
 */
@FunctionalInterface
public interface CellReadExCallback {

    void call(ExcelRowDef rowDefinition, ExcelCellDef cellDefinition, Exception ex);
}
