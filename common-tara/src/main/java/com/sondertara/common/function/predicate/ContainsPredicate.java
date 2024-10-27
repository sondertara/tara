package com.sondertara.common.function.predicate;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.comparator.EqualsComparator;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Predicate;

public class ContainsPredicate<V> implements Predicate<V> {
    private TreeSet<V> set;

    public ContainsPredicate(Collection<V> collection) {
        this(collection,null);
    }

    public ContainsPredicate(Collection<V> collection, @NonNull Comparator<V> comparator) {
        if (comparator == null) {
            comparator = new EqualsComparator<V>();
        }
        set = new TreeSet<V>(comparator);
        if (ObjectUtils.isNotEmpty(collection)) {
            set.addAll(collection);
        }
    }

    @Override
    public boolean test(V value) {
        return set.contains(value);
    }
}
