package com.sondertara.excel.context;

/**
 * @author huangxiaohu
 */
public interface ExcelWriterContext<T> extends ExcelContext<T> {
    /**
     * 移除Sheet定义
     *
     * @param index the index of sheet
     */
    void removeSheet(int index);
}
