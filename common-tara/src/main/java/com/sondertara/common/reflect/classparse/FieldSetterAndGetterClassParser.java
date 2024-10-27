package com.sondertara.common.reflect.classparse;


import com.sondertara.common.base.Assert;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.reflect.Modifiers;
import com.sondertara.common.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FieldSetterAndGetterClassParser<F extends FieldInfo> implements ClassParser<Map<String, F>> {
    private boolean hierachial = false;
    private boolean zeroParameterConstructor = false;

    public FieldSetterAndGetterClassParser() {
    }

    public boolean isHierachial() {
        return hierachial;
    }

    public void setHierachial(boolean hierachial) {
        this.hierachial = hierachial;
    }

    public boolean isZeroParameterConstructor() {
        return zeroParameterConstructor;
    }

    public void setZeroParameterConstructor(boolean zeroParameterConstructor) {
        this.zeroParameterConstructor = zeroParameterConstructor;
    }

    private boolean isParsable(Class clazz) {
        return clazz != null && clazz != Object.class && !clazz.isInterface() && !clazz.isArray() && !clazz.isAnnotation() && !clazz.isPrimitive();
    }

    @Override
    public Map<String, F> parse(Class clazz) {
        return parse0(clazz, zeroParameterConstructor);
    }

    private Map<String, F> parse0(final Class clazz, boolean checkZeroParameterConstructor) {
        Objects.requireNonNull(clazz);
         Assert.isTrue(!clazz.isInterface());
         Assert.isTrue(Object.class != clazz);

        // zero parameter constructor

        final Map<String, F> fieldInfoMap = new HashMap<String, F>();
        Collection<Field> fields = ReflectUtils.getAllDeclaredFields(clazz,true);
        CollectionUtils.forEach(fields, new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                F fieldInfo = parseField(clazz, field);
                if (fieldInfo != null) {
                    fieldInfoMap.put(field.getName(), fieldInfo);
                }
            }
        });

        if (hierachial) {
            Class parentClass = clazz.getSuperclass();
            if (isParsable(parentClass)) {
                CollectionUtils.forEach(parse0(parentClass, false), new BiConsumer<String, F>() {
                    @Override
                    public void accept(String fieldName, F fieldInfo) {
                        if (!fieldInfoMap.containsKey(fieldName)) {
                            fieldInfoMap.put(fieldName, fieldInfo);
                        }
                    }
                });
            }
        }
        return fieldInfoMap;
    }

    protected F parseField(Class clazz, Field field) {
        if (Modifiers.isStatic(field)) {
            return null;
        }

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setField(field);
        Class fieldType = field.getType();

        // setter
        Method setter = ReflectUtils.getSetter(clazz, field.getName(), fieldType);

        // getter
        Method getter = ReflectUtils.getGetter(clazz, field.getName());

        fieldInfo.setGetter(getter);

        fieldInfo.setSetter(setter);
        return (F) fieldInfo;
    }
}
