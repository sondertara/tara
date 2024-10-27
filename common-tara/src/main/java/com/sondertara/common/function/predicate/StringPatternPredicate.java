package com.sondertara.common.function.predicate;

import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class StringPatternPredicate implements Predicate<String> {
    private Regexp regexp;

    public StringPatternPredicate(Pattern pattern) {
        Objects.requireNonNull(pattern);
        this.regexp = RegexUtils.createRegexp(pattern);
    }

    public StringPatternPredicate(String regexp, int flags) {
        this(Pattern.compile(regexp, flags));
    }


    public StringPatternPredicate(String regexp) {
        this(regexp, 0);
    }

    @Override
    public boolean test(String value) {
        return regexp.matcher(value).matches();
    }
}
