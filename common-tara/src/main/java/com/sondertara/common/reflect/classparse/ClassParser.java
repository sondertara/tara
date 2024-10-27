package com.sondertara.common.reflect.classparse;

import com.sondertara.common.function.Parser;

public interface ClassParser<R> extends Parser<Class, R> {
    @Override
    R parse(Class clazz);
}
