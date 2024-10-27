package com.sondertara.common.text.stringtemplate;

import com.sondertara.common.regex.Regexp;

import java.util.function.BiFunction;


/**
 * pattern: {0},{1},{2},{3}
 * start index: 0
 * @author huangxiaohu.1ih
 */
public class IndexStringFormatter extends CustomPatternStringFormatter {

    public IndexStringFormatter() {
        this.setVariablePattern(null);
        setValueGetter(null);
    }

    @Override
    public void setVariablePattern(Regexp variablePattern) {
        super.setVariablePattern(StringTemplate.DEFAULT_PATTERN);
    }

    @Override
    public void setValueGetter(BiFunction<String, Object[], String> valueGetter) {
        super.setValueGetter(new StringTemplate.IndexBasedValueGetter());
    }

}
