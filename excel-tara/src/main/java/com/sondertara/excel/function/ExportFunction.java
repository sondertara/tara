package com.sondertara.excel.function;

import java.util.List;

/**
 * 分页查询
 *
 * @param <T>
 * @author huangxiaohu
 */
@FunctionalInterface
public interface ExportFunction<T> {

    List<T> queryPage(Integer pageNo, Integer pageSize);
}
