
package com.sondertara.common.bean;

import com.sondertara.common.convert.GenericConvert;

import java.lang.reflect.Field;

class SingleCopier extends AbstractCopier {

    SingleCopier(Field fromField, Field toField) {
        super(fromField, toField);
        fromField.setAccessible(true);
        toField.setAccessible(true);
        Class<?> fromCls = fromField.getType();
        Class<?> toCls = toField.getType();
        if (!toCls.isAssignableFrom(fromCls)) {
            converter = ConverterRegistry.find(fromCls.getName(), toCls.getName());
            if (converter == null) {
                converter = new GenericConvert(toField.getGenericType());
                // throw new BeanAnalysisException(String.format("Converter not found. from: %s,
                // to: %s", fromCls.getName(), toCls.getName()));
            }
        }
    }

    @Override
    public String toString() {
        return "SingleCopier{" + "fromField=" + fromField + ", toField=" + toField + ", converter=" + converter + '}';
    }
}
