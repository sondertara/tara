package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.exception.BeanCopyException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

class CollectionCopier extends AbstractCopier {
    private final boolean isSet;

    CollectionCopier(Field fromField, Field toField, String fromEtlType, String toEtlType, boolean isSet) {
        super(fromField, toField);
        this.isSet = isSet;
        fromField.setAccessible(true);
        toField.setAccessible(true);
        converter = Utils.findOrCreateConverter(fromEtlType, toEtlType);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void copy(Object source, Object target) {
        try {
            Collection fromColl = (Collection) fromField.get(source);
            if (fromColl == null) {
                if (COPY_IGNORE_NULL.get()) {
                    return;
                }
                toField.set(target, null);
                return;
            }
            Collection toColl = (Collection) toField.get(target);
            if (toColl == null) {
                if (isSet) {
                    toColl = new HashSet();
                } else {
                    toColl = new ArrayList();
                }
                toField.set(target, toColl);
            }

            if (converter == null) {
                toColl.addAll(fromColl);
            } else {
                for (Object elem : fromColl) {
                    toColl.add(converter.convert(elem, null));
                }
            }
        } catch (IllegalAccessException e) {
            throw new BeanCopyException(e);
        }
    }

    @Override
    public String toString() {
        return "CollectionCopier{" + "fromField=" + fromField + ", toField=" + toField + ", converter=" + converter + ", isSet=" + isSet + '}';
    }
}
