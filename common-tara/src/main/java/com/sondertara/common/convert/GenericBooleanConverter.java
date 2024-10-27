package com.sondertara.common.convert;

import com.sondertara.common.function.TypeConverter;

/**
 * 通用布尔转换器
 *
 * @author huangxiaohu.1ih
 */
public class GenericBooleanConverter implements TypeConverter<Boolean> {
    private static final BooleanEvaluator BOOLEAN_EVALUATOR = BooleanEvaluator.createFalseEvaluator(false, true, new Object[]{"false", 0, false, "off", 'n', "no"});
    public static final GenericBooleanConverter INSTANCE = new GenericBooleanConverter();

    @Override
    public Boolean apply(Object input) {
        return BOOLEAN_EVALUATOR.evalTrue(input);
    }
}