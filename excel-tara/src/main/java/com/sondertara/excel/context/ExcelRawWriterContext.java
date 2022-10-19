package com.sondertara.excel.context;

import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.model.TaraSheet;

import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public interface ExcelRawWriterContext<T> extends ExcelWriterContext<T> {
    /**
     * 获取sheet 定义
     *
     * @return the map
     */
   Map<String, ? extends TaraSheet> getSheetDefinitions();

    /**
     * 添加数据
     *
     * @param dataList 数据
     */
    void addData(List<?> dataList);


    /**
     * 添加模型元数据
     *
     * @param clazz 模型类
     */
    void addMapper(Class<?> ...clazz);


    /**
     * add query for Excel class
     *
     * @param excelClass class
     * @param function   the query function
     */

    void addMapper(Class<?> excelClass, ExportFunction<?> function);

}
