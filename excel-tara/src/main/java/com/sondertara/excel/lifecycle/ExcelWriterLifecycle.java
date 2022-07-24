package com.sondertara.excel.lifecycle;

/**
 * @author huangxiaohu
 */
public interface ExcelWriterLifecycle {
    /**
     * before callback
     */
    void beforeCallback();

    /**
     * sheet分页
     */
    void sheetPaging();

    /**
     * 处理复杂表头
     */
    void handleComplexHeader();

    /**
     * 添加校验的下拉数据
     */
    void addDataValidation();

    /**
     * 初始化标题
     */
    void initHeadTitle();

    /**
     * 初始化数据
     *
     * @throws IllegalAccessException e
     */
    void initData() throws IllegalAccessException;

    /**
     * after callback
     */
    void afterCallback();

}
