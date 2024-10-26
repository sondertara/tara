
package com.sondertara.common.bean.copier;

import com.sondertara.common.convert.ConvertUtils;
import com.sondertara.common.convert.SimpleConverter;

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
                converter = ConvertUtils.findTypeConvert(fromCls, toCls);
                if (null == converter) {
                    converter = new SimpleConverter(toField.getGenericType());
                }
                ConverterRegistry.put(fromCls.getName(), toCls.getName(), converter);
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
