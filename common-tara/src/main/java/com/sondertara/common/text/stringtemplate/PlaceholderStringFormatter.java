package com.sondertara.common.text.stringtemplate;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.regex.RegexUtils;

import java.util.function.BiFunction;

/**
 * placeholder: {}
 * @author huangxiaohu.1ih
 */
public class PlaceholderStringFormatter extends CustomPatternStringFormatter {

    public PlaceholderStringFormatter() {
        setValueGetter(null);
        setVariablePattern(RegexUtils.compile("\\{}"));
    }

    @Override
    public void setValueGetter(BiFunction<String, Object[], String> valueGetter) {
        super.setValueGetter(new BiFunction<String, Object[], String>() {
            int index = -1;

            @Override
            public String apply(String matched, Object[] args) {
                index++;
                Object value = args[index];
                return Emptys.isNull(value) ? "" : value.toString();
            }
        });
    }

}