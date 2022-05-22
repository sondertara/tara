package com.sondertara.excel.support.callback;


import com.sondertara.excel.meta.model.ExcelRowDefinition;

/**
 * 行解析异常回调
 *
 * @author chenzw
 * @since 1.0.5
 */
public interface ExcelRowReadExceptionCallback {

    void call(ExcelRowDefinition rowDefinition, Exception ex);
}
