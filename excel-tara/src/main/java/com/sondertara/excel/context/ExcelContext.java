package com.sondertara.excel.context;

import com.sondertara.excel.executor.TaraExcelExecutor;

/**
 * Excel Context
 * @author huangxiaohu
 */
public interface ExcelContext<T> {

    /**
     * Get the executor
     * 获取执行器
     *
     * @return the executor
     */
    TaraExcelExecutor<T> getExecutor();

}
