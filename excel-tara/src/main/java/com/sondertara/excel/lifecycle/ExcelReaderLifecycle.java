package com.sondertara.excel.lifecycle;

import com.sondertara.excel.meta.model.ExcelRowDef;

/**
 * @author huangxiaohu
 */
public interface ExcelReaderLifecycle {

    /**
     * 是否空行
     *
     * @param row
     * @return
     */
    boolean isEmptyRow(ExcelRowDef row);

    /**
     * 前置设置
     *
     * @return
     * @since 1.0.5
     */
    void preSet(ExcelRowDef row);

    /**
     * 数据校验
     *
     * @param row
     * @return
     */
    boolean validate(ExcelRowDef row);

    /**
     * 格式转换
     *
     * @param row
     * @return
     */
    void format(ExcelRowDef row);

}
