package com.sondertara.excel.support.callback.impl;

import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.support.callback.RowReadExCallback;

/**
 * @author huangxiaohu
 */
public class DefaultRowReadExCallback implements RowReadExCallback {

    @Override
    public void call(final ExcelRowDef rowDefinition, final Exception ex) {
        if (ex instanceof ExcelReaderException) {
            throw (ExcelReaderException) ex;
        }
        throw new ExcelReaderException(ex);
    }
}
