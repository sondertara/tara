package com.sondertara.excel.function;

import java.util.List;
import java.util.function.Function;

/**
 * 分页查询
 *
 * @param <T>
 * @author huangxiaohu
 */
public interface ExportFunction<T>  extends Function<Integer, List<T>> {
}
