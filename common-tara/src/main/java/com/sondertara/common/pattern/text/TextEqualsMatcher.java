package com.sondertara.common.pattern.text;


import com.sondertara.common.text.StringUtils;

/**
 *  */
public class TextEqualsMatcher extends TextPatternMatcher {
    @Override
    public boolean doMatch(String string) {
        return StringUtils.equals(string, pattern, option.isIgnoreCase());
    }
}
