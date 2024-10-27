package com.sondertara.common.reflect.annotation;

import com.sondertara.common.function.Parser;

import java.lang.annotation.Annotation;

public interface AnnotationParser<A extends Annotation, I, O> extends Parser<I, O> {
    Class<A> getAnnotation();

    @Override
    O parse(I input);

}
