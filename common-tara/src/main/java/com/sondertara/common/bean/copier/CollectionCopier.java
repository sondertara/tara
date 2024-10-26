package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.exception.BeanCopyException;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.Sets;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void copy(Object source, Object target, String... ignoreProperties) {
        try {
            Set<String> ignoredSet = Arrays.stream(ignoreProperties).collect(Collectors.toSet());
            if (ignoredSet.contains(fromField.getName())) {
                return;
            }
            Collection fromColl = (Collection) fromField.get(source);
            if (fromColl == null) {
                toField.set(target, null);
                return;
            }
            Collection toColl = (Collection) toField.get(target);
            if (toColl == null) {
                if (isSet) {
                    toColl = Sets.asSet(null, Sets.SetType.ofSet((Set) fromField.get(source)));
                } else {
                    toColl = Lists.asList(null, Lists.ListType.ofList((List) fromField.get(source)));
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
