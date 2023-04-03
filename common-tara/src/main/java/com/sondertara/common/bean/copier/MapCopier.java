package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.exception.BeanCopyException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class MapCopier extends AbstractCopier {

    MapCopier(Field fromField, Field toField, String fromEtlType, String toEtlType) {
        super(fromField, toField);
        fromField.setAccessible(true);
        toField.setAccessible(true);
        converter = Utils.findOrCreateConverter(fromEtlType, toEtlType);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void copy(Object source, Object target) {
        try {
            Map fromMap = (Map) fromField.get(source);
            if (fromMap == null) {
                if (COPY_IGNORE_NULL.get()) {
                    return;
                }
                toField.set(target, null);
                return;
            }
            Map toMap = (Map) toField.get(target);
            if (toMap == null) {
                toMap = new HashMap<>(8);
                toField.set(target, toMap);
            }

            if (converter == null) {
                toMap.putAll(fromMap);
            } else {
                for (Object entryObj : fromMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) entryObj;
                    toMap.put(entry.getKey(), converter.convert(entry.getValue(), null));
                }
            }
        } catch (IllegalAccessException e) {
            throw new BeanCopyException(e);
        }
    }
}
