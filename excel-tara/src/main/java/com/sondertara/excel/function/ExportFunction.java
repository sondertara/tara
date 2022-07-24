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

    PageResult<T> queryPage(Integer pageNo, Integer pageSize);
}
