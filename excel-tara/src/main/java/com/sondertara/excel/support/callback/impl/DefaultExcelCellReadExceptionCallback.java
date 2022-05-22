package com.sondertara.excel.support.callback.impl;


import com.sondertara.excel.exception.ExcelException;
import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.meta.model.ExcelCellDefinition;
import com.sondertara.excel.meta.model.ExcelRowDefinition;
import com.sondertara.excel.support.callback.ExcelCellReadExceptionCallback;

/**
 * @author chenzw
 * @since 1.0.3
 */
public class DefaultExcelCellReadExceptionCallback implements ExcelCellReadExceptionCallback {

    @Override
    public void call(ExcelRowDefinition rowDefinition, ExcelCellDefinition cellDefinition, Exception ex) {
        if (ex instanceof ExcelReaderException) {
            throw (ExcelReaderException) ex;
        }
       throw new ExcelException(ex.getMessage(), ex);
    }
}
