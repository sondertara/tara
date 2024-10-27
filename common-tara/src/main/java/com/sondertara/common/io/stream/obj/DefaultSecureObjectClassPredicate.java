package com.sondertara.common.io.stream.obj;

import com.sondertara.common.function.Functions;

import java.io.ObjectStreamClass;
import java.util.function.Predicate;

/**
 *  */
public final class DefaultSecureObjectClassPredicate implements SecureObjectClassPredicate {
    private Predicate<String> classNameMatcher = Functions.booleanPredicate(true);

    public DefaultSecureObjectClassPredicate() {
    }

    public DefaultSecureObjectClassPredicate(Predicate<String> matcher) {
        this.classNameMatcher = matcher;
    }


    @Override
    public boolean test(ObjectStreamClass osc) {
        return classNameMatcher.test(osc.getName());
    }
}
