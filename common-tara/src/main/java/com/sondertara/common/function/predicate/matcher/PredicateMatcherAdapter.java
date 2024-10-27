package com.sondertara.common.function.predicate.matcher;

import com.sondertara.common.function.Matcher;

import java.util.function.Predicate;

public class PredicateMatcherAdapter<E> implements Matcher<E, Boolean> {
    private Predicate<E> predicate;

    public PredicateMatcherAdapter(Predicate<E> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Boolean matches(E e) {
        return predicate.test(e);
    }
}
