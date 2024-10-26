package com.sondertara.excel.lifecycle;

/**
 * @author huangxiaohu
 */
public interface ExcelWriterLifecycle<T> {
    /**
     * before callback
     */
    void beforeCallback();


    /**
     * 处理复杂表头
     */
    void handleComplexHeader(T sheet,String sheetIdentity);

    /**
     * 添加校验的下拉数据
     */
    void addDataValidation(T sheet,String sheetIdentity);

    /**
     * 初始化标题
     */
    void initHeadTitle(T sheet,String sheetIdentity);

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
