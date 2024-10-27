package com.sondertara.common.comparator;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public interface OrderedComparator<T> extends Comparator<T>, Function<T, Integer> {


    @Override
    default int compare(T o1, T o2) {
        Integer left = Optional.ofNullable(apply(o1)).orElse(0);
        Integer right = Optional.ofNullable(apply(o2)).orElse(0);
        return Integer.compare(left, right);
    }
}
