package com.sondertara.excel.lifecycle;

public interface ExcelWriterLifecycle {

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
     * @throws IllegalAccessException
     */
    void initData() throws IllegalAccessException;

    void afterCallback();

}
