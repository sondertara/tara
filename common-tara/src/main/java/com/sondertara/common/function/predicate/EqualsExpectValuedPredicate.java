package com.sondertara.common.function.predicate;

import com.sondertara.common.base.ObjectUtils;

public class EqualsExpectValuedPredicate<V> extends ExpectValuedPredicate<V> {
    @Override
    protected boolean doTest(V actualValue) {
        return ObjectUtils.equals(actualValue, getExpectedValue());
    }
}
