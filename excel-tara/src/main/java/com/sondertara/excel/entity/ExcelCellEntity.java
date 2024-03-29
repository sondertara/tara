package com.sondertara.excel.entity;

import com.sondertara.excel.meta.annotation.ExcelDataFormat;
import com.sondertara.excel.meta.style.CellStyleBuilder;
import com.sondertara.excel.support.validator.ValueRangeValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Field;
import java.math.RoundingMode;

/**
 * @author huangxiaohu
 */
@Getter
@Setter
@Builder
public class ExcelCellEntity {

    /**
     * excelModel字段Field
     */
    private Field fieldEntity;
    /**
     * excel列名称
     */
    private String columnName;

    private Integer colWidth;
    /**
     * 对应excel中列序
     */
    private Integer index;

    private Boolean authWith;

    private CellType cellType;
    /**
     * 默认单元格值
     */
    private String defaultValue;

    private ExcelDataFormat dateFormat;

    private CellStyleBuilder dataStyle;
    private CellStyleBuilder headStyle;
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

    private ValueRangeValidator rangeValidator;
}
