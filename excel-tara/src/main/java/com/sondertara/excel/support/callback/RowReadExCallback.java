package com.sondertara.excel.support.callback;

import com.sondertara.excel.meta.model.ExcelRowDef;

/**
 * 行解析异常回调
 *
 * @author huangxiaohu
 * @since 1.0.5
 */

@FunctionalInterface
public interface RowReadExCallback {

    void call(ExcelRowDef rowDefinition, Exception ex);
}
