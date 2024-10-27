package com.sondertara.common.text.stringtemplate;

import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;

import java.util.function.BiFunction;



public class CustomPatternStringFormatter implements StringTemplateFormatter {
    private Regexp variableRegexp;
    private BiFunction<String, Object[], String> valueGetter;

    public CustomPatternStringFormatter() {
    }

    public CustomPatternStringFormatter(Regexp variableRegexp, BiFunction<String, Object[], String> valueGetter) {
        setVariablePattern(variableRegexp);
        setValueGetter(valueGetter);
    }

    public CustomPatternStringFormatter(String pattern, BiFunction<String, Object[], String> valueGetter) {
        this(RegexUtils.compile(pattern), valueGetter);
    }


    @Override
    public String format(String template, Object... args) {
        return new StringTemplate().variablePattern(variableRegexp).using(template).with(valueGetter).format(args);
    }

    public Regexp getVariablePattern() {
        return variableRegexp;
    }


    public void setVariablePattern(Regexp variablePattern) {
        this.variableRegexp = variablePattern;
    }

    public BiFunction<String, Object[], String> getValueGetter() {
        return valueGetter;
    }

    public void setValueGetter(BiFunction<String, Object[], String> valueGetter) {
        this.valueGetter = valueGetter;
    }
}
