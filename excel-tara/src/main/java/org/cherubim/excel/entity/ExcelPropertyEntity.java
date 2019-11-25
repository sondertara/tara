
package org.cherubim.excel.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * @author huangxiaohu
 */
@Getter
@Setter
@Builder
public class ExcelPropertyEntity {

    /**
     * excelModel字段Field
     */
    private Field fieldEntity;
    /**
     * excel列名称
     *
     * @return
     */
    private String columnName;
    /**
     * 对应excel中列序
     */
    private Integer index;
    /**
     * 默认单元格值
     *
     * @return
     */
    private String templateCellValue;
    /**
     * 日期格式 默认 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    private String dateFormat;
    /**
     * 正则表达式校验
     *
     * @return
     */
    private String regex;
    /**
     * 正则表达式校验失败返回的错误信息,regex配置后生效
     *
     * @return
     */
    private String regexMessage;
    /**
     * BigDecimal精度 默认:2
     *
     * @return
     */
    private Integer scale;
    /**
     * BigDecimal 舍入规则 默认:2
     *
     * @return
     */
    private Integer roundingMode;
    /**
     * @return 是否必填
     */
    private Boolean required;
}
