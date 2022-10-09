
package com.sondertara.excel.function;

import com.sondertara.excel.entity.ErrorEntity;

/**
 * @author huangxiaohu
 */
@FunctionalInterface
public interface ImportErrorResolver {
    /**
     * 解析错误
     *
     * @param errorEntity
     */
    void error(ErrorEntity errorEntity);
}