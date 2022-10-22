package com.sondertara.excel.support.callback.impl;

import com.sondertara.excel.exception.ExcelAnnotaionReaderException;
import com.sondertara.excel.exception.ExcelException;
import com.sondertara.excel.meta.model.ExcelCellDef;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.support.callback.CellReadExCallback;

/**
 * @author huangxiaohu
 * @since 1.0.3
 */
public class DefaultCellReadExCallback implements CellReadExCallback {

    @Override
    public void call(ExcelRowDef rowDefinition, ExcelCellDef cellDefinition, Exception ex) {
        if (ex instanceof ExcelAnnotaionReaderException) {
            throw (ExcelAnnotaionReaderException) ex;
        }
        throw new ExcelException(ex.getMessage(), ex);
    }
}
