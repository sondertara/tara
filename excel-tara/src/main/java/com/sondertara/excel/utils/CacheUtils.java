package com.sondertara.excel.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sondertara.excel.support.converter.AbstractExcelColumnConverter;
import com.sondertara.excel.support.validator.AbstractExcelColumnValidator;

import java.lang.annotation.Annotation;
import java.util.List;

public class CacheUtils {
    private static final Cache<String, List<AbstractExcelColumnValidator<Annotation>>> columnValidatorCache = CacheBuilder
            .newBuilder().build();

    private static final Cache<String, List<AbstractExcelColumnConverter<Annotation, ?>>> columnConverterCache = CacheBuilder
            .newBuilder().build();

    public static Cache<String, List<AbstractExcelColumnValidator<Annotation>>> getColValidatorCache() {
        return columnValidatorCache;
    }

    public static Cache<String, List<AbstractExcelColumnConverter<Annotation, ?>>> getColConverterCache() {
        return columnConverterCache;
    }

}
