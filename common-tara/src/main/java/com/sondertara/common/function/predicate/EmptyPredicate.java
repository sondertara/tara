package com.sondertara.common.function.predicate;

import com.sondertara.common.base.ObjectUtils;

import java.util.function.Predicate;

public class EmptyPredicate<V> implements Predicate<V> {
    public static final EmptyPredicate IS_EMPTY_PREDICATE = new EmptyPredicate();
    public static final EmptyPredicate IS_NOT_EMPTY_PREDICATE = new EmptyPredicate(false);

    private boolean judgeEmpty;

    public EmptyPredicate() {
        this(true);
    }

    public EmptyPredicate(boolean judgeEmpty) {
        this.judgeEmpty = judgeEmpty;
    }

    @Override
    public boolean test(V value) {
        return judgeEmpty ? ObjectUtils.isEmpty(value) : ObjectUtils.isNotEmpty(value);
    }
}
