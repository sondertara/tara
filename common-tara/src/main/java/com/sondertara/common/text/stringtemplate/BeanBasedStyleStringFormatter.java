package com.sondertara.common.text.stringtemplate;

import com.sondertara.common.accessor.Accessor;
import com.sondertara.common.reflect.FieldAccessor;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;

import java.util.Objects;
import java.util.function.BiFunction;

public class BeanBasedStyleStringFormatter extends CustomPatternStringFormatter {
    static final Regexp BEAN_VARIABLE_PATTERN = RegexUtils.compile("\\$\\{[a-zA-Z_]\\w*}");


    public BeanBasedStyleStringFormatter() {
        super(BEAN_VARIABLE_PATTERN, new FieldValueGetter());
    }

    private static class FieldValueGetter implements BiFunction<String, Object[], String> {
        @Override
        public String apply(String matched, Object[] args) {
            Objects.requireNonNull(args);
            if(matched.startsWith("${") && matched.endsWith("}")) {
                matched = matched.substring(2, matched.length() - 1);
            }
            Accessor<String, ?> accessor = new FieldAccessor(args[0]);
            return accessor.getString(matched);
        }
    }
}
