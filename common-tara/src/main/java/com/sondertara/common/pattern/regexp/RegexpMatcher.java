package com.sondertara.common.pattern.regexp;

import com.sondertara.common.base.Assert;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.io.resource.PathMatcher;
import com.sondertara.common.pattern.AbstractPatternMatcher;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;


public class RegexpMatcher extends AbstractPatternMatcher implements PathMatcher {

    private String pattern;
    private Regexp regexp;

    public RegexpMatcher() {
    }

    public RegexpMatcher(String pattern) {
        this(pattern, false);
    }

    public RegexpMatcher(String pattern, boolean ignoreCase) {
        this(pattern, ignoreCase, true);
    }

    public RegexpMatcher(String pattern, boolean ignoreCase, boolean trimPattern) {
        setPatternExpression(pattern);
        setIgnoreCase(ignoreCase);
        setTrimPattern(trimPattern);
    }

    @Override
    public void setPatternExpression(String patternExpression) {
        this.pattern = patternExpression;
    }

    @Override
    public Boolean matches(String string) {
        Assert.notEmpty(string, "the string is null or empty");
        Assert.notEmpty(pattern, "the regexp is null or empty");

        if (regexp == null) {
            if (trimPattern) {
                pattern = StringUtils.trim(pattern);
                if (StringUtils.isEmpty(pattern)) {
                    throw new IllegalArgumentException("illegal regexp pattern");
                }
            }

            regexp = RegexUtils.createRegexp(pattern, this.option);
        }

        if (trimPattern) {
            string = StringUtils.trim(string);
        }
        return regexp.matcher(string).matches();
    }
}
