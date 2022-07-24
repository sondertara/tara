package com.sondertara.excel.support.callback.impl;


import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.meta.model.ExcelRowDefinition;
import com.sondertara.excel.support.callback.ExcelRowReadExceptionCallback;

/**
 * @author huangxiaohu
 */
public class DefaultExcelRowReadExceptionCallback implements ExcelRowReadExceptionCallback {

    @Override
    public void call(final ExcelRowDefinition rowDefinition, final Exception ex) {
        if(ex instanceof ExcelReaderException){
            throw (ExcelReaderException) ex;
        }
        throw new ExcelReaderException(ex);
    }
}
