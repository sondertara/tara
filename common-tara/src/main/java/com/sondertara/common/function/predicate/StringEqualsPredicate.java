package com.sondertara.common.function.predicate;

import com.sondertara.common.text.StringUtils;

import java.util.function.Predicate;

public class StringEqualsPredicate implements Predicate<String> {
    private boolean ignoreCase;
    private String expected;

    public StringEqualsPredicate(String expected) {
        this(expected, true);
    }

    public StringEqualsPredicate(String expected, boolean ignoreCase) {
        this.expected = expected;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean test(String value) {
        return StringUtils.equals(value, expected, ignoreCase);
    }
}
