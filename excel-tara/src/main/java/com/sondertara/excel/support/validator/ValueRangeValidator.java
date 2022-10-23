package com.sondertara.excel.support.validator;

import com.sondertara.common.regex.PatternPool;
import com.sondertara.common.util.LocalDateTimeUtils;
import com.sondertara.common.util.RegexUtils;
import com.sondertara.common.util.StringFormatter;
import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.enums.FieldRangeType;
import com.sondertara.excel.exception.ExcelValidationException;
import com.sondertara.excel.meta.annotation.validation.ExcelRangeRule;

import java.math.BigDecimal;

/**
 * @author huangxiaohu
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ValueRangeValidator implements AbstractExcelColumnValidator<ExcelRangeRule> {

    private FieldRangeType rangeType;

    private String min;
    private String max;

    private Comparable left;
    private Comparable right;
    private Comparable current;


    @Override
    public void initialize(ExcelRangeRule annotation) {
        this.max = annotation.max();
        this.min = annotation.min();
        this.rangeType = annotation.rangeType();

    }

    @Override
    public boolean validate(String value) {
        if (StringUtils.isBlank(min) && StringUtils.isBlank(max)) {
            return true;
        }
        parseValue(value);
        String message = null;
        switch (rangeType) {
            case RANGE_CLOSE:
                if (illegalLeft(current, left, true)) {
                    message = StringFormatter.format("Cell value[{}] must greater than min[{}]", current, min);
                } else {
                    if (illegalRight(current, right, true)) {
                        message = StringFormatter.format("Cell value[{}] must less than max[{}]", current, max);
                    }
                }
                break;
            case RANGE_RIGHT_OPEN:
                if (illegalLeft(current, left, true)) {
                    message = StringFormatter.format("Cell value[{}] must greater than min[{}]", current, min);
                } else {
                    if (illegalRight(current, right, false)) {
                        message = StringFormatter.format("Cell value[{}] must less than max[{}]", current, max);

                    }
                }
                break;
            case RANGE_LEFT_OPEN:
                if (illegalLeft(current, left, false)) {
                    message = StringFormatter.format("Cell value[{}] must greater than min[{}]", current, min);
                } else {
                    if (illegalRight(current, right, true)) {
                        message = StringFormatter.format("Cell value[{}] must less than max[{}]", current, max);

                    }
                }
                break;
            case RANGE_OPEN:
                if (illegalLeft(current, left, false)) {
                    message = StringFormatter.format("Cell value[{}] must greater than min[{}]", current, min);
                } else {
                    if (illegalRight(current, right, false)) {
                        message = StringFormatter.format("Cell value[{}] must less than max[{}]", current, max);

                    }
                }
                break;
            default:
        }
        if (null != message) {
            throw new ExcelValidationException(message);
        }
        return true;
    }

    private <T extends Comparable<T>> boolean illegalLeft(T target, T boundary, boolean equals) {
        if (boundary == null) {
            return false;
        }
        if (equals) {
            return boundary.compareTo(target) > 0;
        } else {
            return boundary.compareTo(target) >= 0;
        }
    }

    private <T extends Comparable<T>> boolean illegalRight(T target, T boundary, boolean equals) {
        if (boundary == null) {
            return false;
        }
        if (equals) {
            return target.compareTo(boundary) > 0;
        } else {
            return target.compareTo(boundary) >= 0;
        }
    }

    private void parseValue(String value) {
        try {
            if (StringUtils.isBlank(min)) {
                if (RegexUtils.isMatch(PatternPool.NUMBERS, min)) {
                    left = new BigDecimal(min);
                    current = new BigDecimal(value);
                } else {
                    left = LocalDateTimeUtils.parseDate(max);
                    current = LocalDateTimeUtils.parseDate(value);
                }
            }
            if (StringUtils.isNotBlank(max)) {
                if (RegexUtils.isMatch(PatternPool.NUMBERS, max)) {
                    right = new BigDecimal(max);
                    if (null == current) {
                        current = new BigDecimal(value);
                    }
                } else {
                    right = LocalDateTimeUtils.parseDate(max);
                    if (null == current) {
                        current = LocalDateTimeUtils.parseDate(value);
                    }
                }
            }
        } catch (Exception e) {
            String message = StringFormatter.format("Excel cell not match the range,the range min[{}],max[{}],the value[{}]", min, max, value);
            throw new ExcelValidationException(message);
        }
    }
}
