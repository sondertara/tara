package com.sondertara.excel.context;

import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.function.ExportFunction;

public abstract class AbstractExcelWriterContext implements ExcelWriterContext {

    public abstract void addMapper(Class<?> excelClass, ExportFunction<?> function, PageQueryParam queryParam);

}
