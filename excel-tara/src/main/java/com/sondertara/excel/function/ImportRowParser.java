package com.sondertara.excel.function;

/**
 * @author huangxiaohu
 */
@FunctionalInterface
public interface ImportRowParser<T> {
    /**
     * parse of row
     *
     * @param sheetIndex sheet
     * @param rowIndex   row num
     * @param entity     the read entity
     */
    void parse(Integer sheetIndex, Integer rowIndex, T entity);
}
