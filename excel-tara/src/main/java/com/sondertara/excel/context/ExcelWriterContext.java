package com.sondertara.excel.context;

import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.model.TaraSheet;

import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public interface ExcelWriterContext extends ExcelContext {
    /**
     * 获取sheet 定义
     *
     * @return the map
     */
    Map<Integer, ? extends TaraSheet> getSheetDefinitions();

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
