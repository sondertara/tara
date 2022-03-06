package com.sondertara.excel.function;

import com.sondertara.excel.entity.PageQueryParam;

import java.util.List;

/**
 * 分页查询
 *
 * @param <T>
 * @param <R>
 * @param <U>
 * @author huangxiaohu
 */
public interface ExportFunction<T extends PageQueryParam, R> {
    /**
     * 分页查询方法
     *
     * @param param    query param
     * @param pageNo   current page no
     * @return 查询结果
     */
    List<R> pageQuery(T param, int pageNo);

    /**
     * 集合内对象转换
     *
     * @param queryResult the query result
     * @return the export data
     */
    Object convert(R queryResult);

}
