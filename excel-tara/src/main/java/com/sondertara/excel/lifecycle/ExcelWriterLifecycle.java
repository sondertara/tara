package com.sondertara.excel.lifecycle;

import org.apache.poi.xssf.streaming.SXSSFSheet;

/**
 * @author huangxiaohu
 */
public interface ExcelWriterLifecycle {
    /**
     * before callback
     */
    void beforeCallback();


    /**
     * 处理复杂表头
     */
    void handleComplexHeader(SXSSFSheet sheet,String sheetIdentity);

    /**
     * 添加校验的下拉数据
     */
    void addDataValidation(SXSSFSheet sheet,String sheetIdentity);

    /**
     * 初始化标题
     */
    void initHeadTitle(SXSSFSheet sheet,String sheetIdentity);

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
