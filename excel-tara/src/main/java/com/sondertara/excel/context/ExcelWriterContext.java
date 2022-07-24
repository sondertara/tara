package com.sondertara.excel.context;

import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.function.ExportFunction;

import java.util.List;

/**
 * @author chenzw
 */
public interface ExcelWriterContext extends ExcelContext {

    /**
     * 添加数据
     *
     * @param dataList 数据
     */
    void addData(List<?>... dataList);

    /**
     * 添加数据
     *
     * @param dataList 数据
     */
    void addData(List<List<?>> dataList);

    /**
     * 添加模型元数据
     *
     * @param clazz 模型类
     */
    void addModel(Class<?>... clazz);

    /**
     * 移除Sheet定义
     *
     * @param index the index of sheet
     */
    void removeSheet(int index);

    void addMapper(Class<?> excelClass, ExportFunction<?> function, PageQueryParam queryParam);


}
