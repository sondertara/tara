
package com.sondertara.excel.function;

import java.util.List;

/**
 * 分页查询
 *
 * @param <P>
 * @param <T>
 * @author huangxiaohu
 */
public interface ExportFunction<P, T> {
    /**
     * 分页查询方法
     *
     * @param param
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<T> pageQuery(P param, int pageNum, int pageSize);

    /**
     * 集合内对象转换
     *
     * @param queryResult
     * @return
     */
    Object convert(T queryResult);

}
