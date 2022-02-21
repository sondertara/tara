
package com.sondertara.excel.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangxiaohu
 */

@Builder
@Data
public class ErrorEntity {
    /**
     * sheet索引
     */
    private Integer sheetIndex;
    /**
     * 行号
     */
    private Integer rowIndex;
    /**
     * 单元格
     */
    private Integer cellIndex;
    /**
     * 单元格内容
     */
    private String cellValue;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 错误信息 fff
     */
    private String errorMessage;
}
