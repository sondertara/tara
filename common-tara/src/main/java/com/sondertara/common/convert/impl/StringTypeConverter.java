package com.sondertara.common.convert.impl;

import com.sondertara.common.convert.AbstractTypeConverter;

/**
 * 字符串格式转换器
 *
 * @author huangxiaohu
 */
public class StringTypeConverter extends AbstractTypeConverter<String> {

    @Override
    protected String convertInternal(Object value) {
        return convertToStr(value);
    }
}
