package com.sondertara.excel.context;


import com.sondertara.excel.executor.TaraExcelExecutor;
import com.sondertara.excel.meta.AnnotationSheet;
import com.sondertara.excel.meta.model.ExcelSheetDefinition;
import com.sondertara.excel.meta.model.TaraSheet;

import java.util.Map;

/**
 * @author chenzw
 */
public interface ExcelContext {
    /**
     * 获取sheet 定义
     *
     * @return the map
     */
    Map<Integer, AnnotationSheet> getSheetDefinitions();

    /**
     * 获取执行器
     *
     * @return the executor
     */
    TaraExcelExecutor<?> getExecutor();


}
