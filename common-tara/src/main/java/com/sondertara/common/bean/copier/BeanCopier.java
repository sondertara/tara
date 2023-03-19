package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.exception.BeanAnalysisException;
import com.sondertara.common.bean.exception.BeanCopyException;
import com.sondertara.common.convert.TypeConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class BeanCopier extends AbstractCopier {
    private final Class<?> fromCls;
    private final Class<?> toCls;
    private Constructor<?> constructor;
    /**
     * No need to be volatile
     */
    private volatile List<Copier> copiers = null;

    /**
     * Top bean
     */
    BeanCopier(Class<?> fromCls, Class<?> toCls) {
        super(null, null);
        this.fromCls = fromCls;
        this.toCls = toCls;
        converter = ConverterRegistry.find(fromCls.getName(), toCls.getName());
        if (converter == null) {
            try {
                constructor = toCls.getDeclaredConstructor();
                constructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new BeanAnalysisException(e);
            }
        }
    }

    /**
     * Referenced bean
     */
    BeanCopier(Field fromField, Field toField) {
        super(fromField, toField);
        this.fromCls = fromField.getType();
        this.toCls = toField.getType();
        fromField.setAccessible(true);
        toField.setAccessible(true);
        converter = ConverterRegistry.find(fromCls.getName(), toCls.getName());
        if (converter == null) {
            try {
                constructor = toCls.getDeclaredConstructor();
                constructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new BeanAnalysisException(e);
            }
        }
    }

    private static List<Copier> analyze(Class<?> sourceCls, Class<?> targetCls) {
        Map<String, Field> fromFieldsMap = new HashMap<>(8);
        for (Field field : allNonStaticFields(sourceCls)) {
            fromFieldsMap.put(field.getName(), field);
        }
        if (fromFieldsMap.isEmpty()) {
            throw new BeanAnalysisException(sourceCls.getName() + " has no copyable field!");
        }

        List<Field> targetFields = allNonStaticFields(targetCls);
        if (targetFields.isEmpty()) {
            throw new BeanAnalysisException(targetCls.getName() + " has no copyable field!");
        }

        List<Copier> copiers = new ArrayList<>();
        for (Field toField : targetFields) {
            Field fromField = fromFieldsMap.get(toField.getName());
            if (fromField == null) {
                continue;
            }
            Type toFieldGType = toField.getGenericType();
            if (toFieldGType instanceof ParameterizedType) {
                Type[] toEtlTypes = ((ParameterizedType) toFieldGType).getActualTypeArguments();
                Type[] fromEtlTypes = ((ParameterizedType) fromField.getGenericType()).getActualTypeArguments();

                Class<?> fieldCls = toField.getType();
                if (Set.class.isAssignableFrom(fieldCls)) {
                    copiers.add(new CollectionCopier(fromField, toField, Utils.nameOf(fromEtlTypes[0]), Utils.nameOf(toEtlTypes[0]), true));
                } else if (Collection.class.isAssignableFrom(fieldCls)) {
                    copiers.add(new CollectionCopier(fromField, toField, Utils.nameOf(fromEtlTypes[0]), Utils.nameOf(toEtlTypes[0]), false));
                } else if (Map.class.isAssignableFrom(fieldCls)) {
                    try {
                        if (!toEtlTypes[0].equals(fromEtlTypes[0]) && !Class.forName(Utils.nameOf(toEtlTypes[0])).isAssignableFrom(Class.forName(Utils.nameOf(fromEtlTypes[0])))) {
                            throw new BeanAnalysisException("Key types mismatch: " + fromField + " <-> " + toField);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new BeanAnalysisException(e);
                    }
                    copiers.add(new MapCopier(fromField, toField, Utils.nameOf(fromEtlTypes[1]), Utils.nameOf(toEtlTypes[1])));
                } else {
                    TypeConverter<?> converter = ConverterRegistry.find(fromField.getType().getName(), toField.getType().getName());
                    if (converter != null) {
                        copiers.add(new SingleCopier(fromField, toField));
                    } else {
                        throw new BeanAnalysisException("Custom generic type requires converter, otherwise is not supported!");
                    }
                }
            } else {
                if (Utils.isBuiltin(fromField.getType()) || Utils.isBuiltin(toField.getType())) {
                    copiers.add(new SingleCopier(fromField, toField));
                } else {
                    copiers.add(BeanCopierRegistry.findOrCreate(fromField, toField));
                }
            }
        }

        if (copiers.isEmpty()) {
            throw new BeanAnalysisException(sourceCls.getName() + " & " + targetCls.getName() + " have no common fields to copy!");
        }
        return copiers;
    }

    private static List<Field> allNonStaticFields(Class<?> cls) {
        List<Field> all = new ArrayList<>();
        Class<?> cur = cls;
        do {
            for (Field each : cur.getDeclaredFields()) {
                if (!Modifier.isStatic(each.getModifiers())) {
                    all.add(each);
                }
            }
        } while ((cur = cur.getSuperclass()) != null);
        return all;
    }

    /**
     * Defer after construction to avoid cyclic reference
     */
    void ensureAnalyzed() {
        if (converter != null) {
            return;
        }
        // DCL without volatile
        if (copiers == null) {
            synchronized (this) {
                if (copiers == null) {
                    copiers = analyze(fromCls, toCls);
                }
            }
        }
    }

    /**
     * Top bean
     */
    Object topCopyWithoutTopConverter(Object source) {
        if (converter != null) {
            return converter.convert(source, null);
        }

        Object target;
        try {
            target = constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new BeanCopyException(e);
        }
        topCopyWithoutTopConverter(source, target);
        return target;
    }

    Object copyConvert(Object source, Object defaultValue) {
        if (converter != null) {
            return converter.convert(source, null);
        }

        Object target;
        try {
            target = constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new BeanCopyException(e);
        }
        topCopyWithoutTopConverter(source, target);
        return target;
    }

    /**
     * Top bean
     */
    void topCopyWithoutTopConverter(Object source, Object target) {
        ensureAnalyzed();
        for (Copier copier : copiers) {
            copier.copy(source, target);
        }
    }

    /**
     * Referenced bean
     */
    @Override
    public void copy(Object source, Object target) {
        Object from, to;
        try {
            from = fromField.get(source);
            to = toField.get(target);
            if (from == null) {
                if (COPY_IGNORE_NULL.get()) {
                    return;
                }
                toField.set(target, null);
                return;
            }

            if (converter != null) {
                toField.set(target, converter.convert(from, null));
                return;
            }

            if (to == null) {
                to = constructor.newInstance();
                toField.set(target, to);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new BeanCopyException(e);
        }

        ensureAnalyzed();
        for (Copier copier : copiers) {
            copier.copy(from, to);
        }
    }

    @Override
    public String toString() {
        ensureAnalyzed();
        return "BeanCopier{" + "fromField=`" + fromField + "`, toField=`" + toField + "`, fromCls=`" + fromCls + "`, toCls=`" + toCls + "`, converter=" + converter + ", copiers=" + join(copiers) + "}";
    }

    private String join(Collection<Copier> items) {
        if (items == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Object item : items) {
            sb.append("\n  ");
            String str = (item instanceof BeanCopier) ? item.getClass().getSimpleName() : item.toString();
            sb.append(str);
        }
        return sb.append("]").toString();
    }
}
