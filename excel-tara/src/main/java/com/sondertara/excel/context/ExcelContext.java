package com.sondertara.excel.context;


import com.sondertara.excel.executor.ExcelExecutor;
import com.sondertara.excel.meta.model.ExcelSheetDefinition;

import java.util.Map;

/**
 * @author chenzw
 */
public interface ExcelContext {

    Map<Integer, ExcelSheetDefinition> getSheetDefinitions();

    ExcelExecutor getExecutor();


}
