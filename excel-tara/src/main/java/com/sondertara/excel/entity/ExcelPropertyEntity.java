
package com.sondertara.excel.entity;

import com.sondertara.excel.enums.FieldRangeType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.math.RoundingMode;

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
     */
    private String columnName;
    /**
     * 对应excel中列序
     */
    private Integer index;
    /**
     * 默认单元格值
     */
    private String templateCellValue;
    /**
     * 日期格式 默认 yyyy-MM-dd HH:mm:ss
     */
    private String dateFormat;
    /**
     * 正则表达式校验
     */
    private String regex;
    /**
     * 正则表达式校验失败返回的错误信息,regex配置后生效
     */
    private String regexMessage;
    /**
     * BigDecimal精度 默认:2
     */
    private Integer scale;
    /**
     * BigDecimal 舍入规则 默认:2
     */
    private RoundingMode roundingMode;
    /**
     * 是否必填
     */
    private Boolean required;
    /**
     * range
     * <p>
     * number  eg:{"2",""}(2, ),{"2","5"},{"","5"}
     * date eg:{"2019-08-01 12:00:00",""},{"2019-08-01 12:00:00","2019-10-01 12:00:00"}
     * </p>
     */
    private String[] range;
    /**
     * range type {@link FieldRangeType} ,default is {@link FieldRangeType#RANGE_CLOSE}
     */
    private FieldRangeType rangeType;
}
