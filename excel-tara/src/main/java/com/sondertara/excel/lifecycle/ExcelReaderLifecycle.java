package com.sondertara.excel.lifecycle;

import com.sondertara.excel.meta.model.ExcelRowDef;

/**
 * @author huangxiaohu
 */
public interface ExcelReaderLifecycle {

    /**
     * 是否空行
     *
     * @param row row def
     * @return
     */
    boolean isEmptyRow(ExcelRowDef row);

    /**
     * 前置设置
     *
     * @param row row
     * @since 1.0.5
     */
    void preSet(ExcelRowDef row);

    /**
     * 数据校验
     *
     * @param row the row
     * @return pass validation
     */
    boolean validate(ExcelRowDef row);

    /**
     * 格式转换
     *
     * @param row row
     */
    void format(ExcelRowDef row);

}
