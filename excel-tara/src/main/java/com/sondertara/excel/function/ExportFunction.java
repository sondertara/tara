package com.sondertara.excel.function;

import com.sondertara.excel.entity.PageResult;

import java.util.List;

/**
 * 分页查询
 *
 * @param <T>
 * @author huangxiaohu
 */
@FunctionalInterface
public interface ExportFunction<T> {
    /**
     * Query Page Data
     *
     * @param index page
     * @return page
     */
   PageResult<T> query(Integer index);
}
