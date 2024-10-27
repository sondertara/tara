package com.sondertara.common.convert.impl;

import com.sondertara.common.function.TypeConverter;

/**
 * @author huangxiaohu.1ih
 */
@SuppressWarnings("rawtypes")
public class NoopConverter implements TypeConverter {
    public static final NoopConverter INSTANCE = new NoopConverter();


    @Override
    public Object apply(Object input) {
        return input;
    }
}
