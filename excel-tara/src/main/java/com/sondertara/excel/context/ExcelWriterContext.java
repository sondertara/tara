package com.sondertara.excel.context;

import java.util.List;

/**
 * @author chenzw
 */
public interface ExcelWriterContext extends ExcelContext {

    /**
     * 添加数据
     * @param datas 数据
     */
    void addData(List<?>... datas);

    /**
     * 添加数据
     * @param datas 数据
     */
    void addData(List<List<?>> datas);

    /**
     * 添加模型元数据
     * @param clazzs 模型类
     */
    void addModel(Class<?>... clazzs);

    /**
     * 移除Sheet定义
     * @param index
     */
    void removeSheet(int index);

}
