package com.sondertara.common.reflect;

import com.sondertara.common.base.Assert;
import com.sondertara.common.comparator.ComparableComparator;
import com.sondertara.common.comparator.DelegatableComparator;
import com.sondertara.common.reflect.type.Primitives;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Objects;

@SuppressWarnings({"unchecked"})
public class FieldComparator<V extends Comparable<V>> implements DelegatableComparator<V> {
    private Field field;
    private Comparator<V> delegate;

    public FieldComparator(@NonNull Field field, @Nullable Comparator<V> fieldComparator) {
        Objects.requireNonNull(field);
        this.field = field;
        if (fieldComparator == null) {
            Class<?> fieldClass = field.getType();
            if (Comparable.class.isAssignableFrom(Primitives.wrap(fieldClass))) {
                fieldComparator = new ComparableComparator<>();
            }
        }
        Assert.notNull(fieldComparator);
        setDelegate(fieldComparator);
    }

    public FieldComparator(@NonNull Class<?> clazz, @NonNull String fieldName, @Nullable Comparator fieldComparator) {
        this(ReflectUtils.getAnyField(clazz, fieldName), fieldComparator);
    }

    @Override
    public int compare(V o1, V o2) {
        V v1 = ReflectUtils.getFieldValue(field, o1, false);
        V v2 = ReflectUtils.getFieldValue(field, o2, false);
        return delegate.compare(v1, v2);
    }

    @Override
    public Comparator<V> getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(@NonNull Comparator<V> delegate) {
        this.delegate = delegate;
    }
}
