package com.sondertara.common.equator;


import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.convert.ConvertUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu
 */
public class MapBaseEquator extends AbstractEquator {
    public MapBaseEquator() {
    }

    public MapBaseEquator(boolean bothExistFieldOnly) {
        super(bothExistFieldOnly);
    }

    public MapBaseEquator(List<String> includeFields, List<String> excludeFields) {
        super(includeFields, excludeFields);
    }

    public MapBaseEquator(List<String> includeFields, List<String> excludeFields, boolean bothExistFieldOnly) {
        super(includeFields, excludeFields, bothExistFieldOnly);
    }

    @Override
    public boolean isEquals(Object first, Object second) {
        return CollectionUtils.isEmpty(getDiffFields(first, second));
    }


    @Override
    public List<DiffFieldInfo> getDiffFields(Object first, Object second) {
        List<DiffFieldInfo> diffFields = new LinkedList<>();
        if (ObjectUtils.equals(first, second)) {
            return diffFields;
        }

        Set<?> allFieldNames;
        Map<?, ?> firstMap = null;
        Map<?, ?> secondMap = null;
        if (first == null) {
            if (!(second instanceof Map)) {
                throw new IllegalArgumentException("Second obj must be map");
            }
            secondMap = ((Map<?, ?>) second);
            allFieldNames = secondMap.keySet();
        } else if (second == null) {
            if (!(first instanceof Map)) {
                throw new IllegalArgumentException("First obj must be map");
            }
            firstMap = ((Map<?, ?>) first);
            allFieldNames = firstMap.keySet();
        } else {
            if (!(second instanceof Map)) {
                throw new IllegalArgumentException("Second obj must be map");
            }
            if (!(first instanceof Map)) {
                throw new IllegalArgumentException("First obj must be map");
            }
            firstMap = ((Map<?, ?>) first);
            secondMap = ((Map<?, ?>) second);
            allFieldNames = getAllFieldNames(firstMap.keySet().stream().map(Object::toString).collect(Collectors.toSet()), secondMap.keySet().stream().map(Object::toString).collect(Collectors.toSet()));
        }


        for (Object key : allFieldNames) {
            Object object = Optional.ofNullable(firstMap).map(s -> s.get(key)).orElse(null);
            Object object1 = Optional.ofNullable(secondMap).map(s -> s.get(key)).orElse(null);
            if (null == object && null != object1) {
                DiffFieldInfo diffFieldInfo = new DiffFieldInfo(key.toString(), null, object1.getClass());
                diffFieldInfo.setFirstVal(null);
                diffFieldInfo.setSecondVal(object1);
                diffFields.add(diffFieldInfo);
                break;
            } else if (null != object && object1 == null) {
                DiffFieldInfo diffFieldInfo = new DiffFieldInfo(key.toString(), object.getClass(), null);
                diffFieldInfo.setFirstVal(object);
                diffFieldInfo.setSecondVal(null);
                diffFields.add(diffFieldInfo);
                break;
            } else if (object1 == null) {
                break;
            }
            if (object instanceof Collection && object1 instanceof Collection) {
                Collection<?> collection = (Collection<?>) object1;
                for (Object o : ((Collection<?>) object)) {
                    if (!collection.contains(o)) {
                        buildFieldInfo(diffFields, key, object, object1);
                        break;
                    }
                }
            } else if (object instanceof Map && object1 instanceof Map) {
                List<DiffFieldInfo> list = getDiffFields(object, object1);
                if (CollectionUtils.isNotEmpty(list)) {
                    buildFieldInfo(diffFields, key, object, object1);
                }
            } else {
                boolean equals = false;
                if (Objects.equals(object1.getClass(), object.getClass())) {
                    equals = Objects.equals(object1, object);
                } else {
                    equals = ObjectUtils.equals(ConvertUtils.convert(object1.getClass(), object), object1);
                }
                if (!equals) {
                    buildFieldInfo(diffFields, key, object, object1);
                }
            }
        }
        return diffFields;
    }

    private void buildFieldInfo(List<DiffFieldInfo> diffFields, Object key, Object firtst, Object second) {
        DiffFieldInfo diffFieldInfo = new DiffFieldInfo(key.toString(), Optional.ofNullable(firtst).map(Object::getClass).orElse(null), Optional.ofNullable(second).map(Object::getClass).orElse(null));
        diffFieldInfo.setFirstVal(firtst);
        diffFieldInfo.setSecondVal(second);
        diffFields.add(diffFieldInfo);
    }
}
