package com.sondertara.common.pattern.text;


import com.sondertara.common.text.StringUtils;

/**
 *  */
public class TextContainsMatcher extends TextPatternMatcher {
    @Override
    public boolean doMatch(String string) {
        return StringUtils.contains(string, pattern, option.isIgnoreCase());
    }
}
