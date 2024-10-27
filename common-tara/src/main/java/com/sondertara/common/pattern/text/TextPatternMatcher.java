package com.sondertara.common.pattern.text;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.pattern.AbstractPatternMatcher;

/**
 * 单纯的文本匹配
 *  */
public abstract class TextPatternMatcher extends AbstractPatternMatcher {
    protected String pattern;

    @Override
    public void setPatternExpression(String patternExpression) {
        this.pattern = patternExpression;
    }

    @Override
    public Boolean matches(String string) {
        if (StringUtils.isEmpty(pattern) && (StringUtils.isEmpty(string))) {
            return true;
        }

        if (StringUtils.isEmpty(pattern) || StringUtils.isEmpty(string)) {
            return false;
        }
        return doMatch(string);

    }

    public abstract boolean doMatch(String string);
}
