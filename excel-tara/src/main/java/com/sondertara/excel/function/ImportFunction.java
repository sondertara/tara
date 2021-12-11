

package com.sondertara.excel.function;

import com.sondertara.excel.entity.ErrorEntity;

/**
 * @author huangxiaohu
 */
public interface ImportFunction<T> {

    /**
     * 导入常规校验通过后执行的操作
     *
     * @param sheetIndex sheet
     * @param rowIndex   row
     * @param entity     the entity
     */
    void onProcess(int sheetIndex, int rowIndex, T entity);

    /**
     * 导入常规校验失败后执行的操作
     *
     * @param errorEntity error
     */
    void onError(ErrorEntity errorEntity);
}