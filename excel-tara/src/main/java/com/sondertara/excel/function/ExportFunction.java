package com.sondertara.excel.function;

import com.sondertara.excel.entity.PageResult;

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
     * @param pageNo page
     * @param pageSize page size
     * @return  page
     */
    PageResult<T> queryPage(Integer pageNo, Integer pageSize);
}
