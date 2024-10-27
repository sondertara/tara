package com.sondertara.common.function.supplier;

import com.sondertara.common.reflect.ReflectUtils;

/**
 *  */
public class ClassNamePrefixSupplier implements PrefixSupplier {
    @Override
    public String apply(Object supplement) {
        if (supplement == null) {
            return "";
        }
        Class clazz = supplement.getClass();
        return ReflectUtils.getSimpleClassName(clazz);
    }
}
