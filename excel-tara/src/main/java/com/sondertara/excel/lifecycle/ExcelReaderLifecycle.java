package com.sondertara.excel.lifecycle;


import com.sondertara.excel.meta.model.ExcelRowDefinition;

/**
 * @author chenzw
 */
public interface ExcelReaderLifecycle {


    /**
     * 是否空行
     *
     * @param row
     * @return
     */
    boolean isEmptyRow(ExcelRowDefinition row);

    /**
     * 前置设置
     *
     * @return
     * @since 1.0.5
     */
    void preSet(ExcelRowDefinition row);


    /**
     * 数据校验
     *
     * @param row
     * @return
     */
    boolean validate(ExcelRowDefinition row);

    /**
     * 格式转换
     *
     * @param row
     * @return
     */
    void format(ExcelRowDefinition row);

}
