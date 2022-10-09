package com.sondertara.excel.context;

import com.sondertara.excel.executor.TaraExcelExecutor;

/**
 * @author huangxiaohu
 */
public interface ExcelContext {

    /**
     * 获取执行器
     *
     * @return the executor
     */
    TaraExcelExecutor<?> getExecutor();

}
