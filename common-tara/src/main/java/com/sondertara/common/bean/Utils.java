
package com.sondertara.common.bean;

import com.sondertara.common.bean.exception.BeanAnalysisException;
import com.sondertara.common.convert.TypeConverter;

import java.lang.reflect.Type;

/**
 * utils fot bean copier
 */
class Utils {
    static boolean isBuiltin(Class<?> cls) {
        return cls.isPrimitive() || cls.getName().startsWith("java.") || cls.getName().startsWith("javax.");
    }

    static String nameOf(Type type) {
        String str = type.toString();
        int idxOfSpace = str.lastIndexOf(' ');
        if (idxOfSpace >= 0) {
            return str.substring(idxOfSpace + 1);
        } else {
            return str;
        }
    }

    static TypeConverter<?> findOrCreateConverter(String fromType, String toType) {
        TypeConverter<?> converter;
        Class<?> fromCls;
        Class<?> toCls;
        try {
            fromCls = Class.forName(fromType);
            toCls = Class.forName(toType);
        } catch (ClassNotFoundException e) {
            throw new BeanAnalysisException(e);
        }
        converter = ConverterRegistry.find(fromType, toType);
        if (converter == null && !toCls.isAssignableFrom(fromCls)) {
            if (!Utils.isBuiltin(fromCls) && !Utils.isBuiltin(toCls)) {
                final BeanCopier beanCopier = BeanCopierRegistry.prepare(fromCls, toCls);
                converter = beanCopier::copyConvert;
            }
            if (converter == null) {
                throw new BeanAnalysisException(
                        String.format("Converter not found. from: %s, to: %s", fromType, toType));
            }
        } // else keep null
        return converter;
    }
}
