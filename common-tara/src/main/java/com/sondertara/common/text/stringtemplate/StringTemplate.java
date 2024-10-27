package com.sondertara.common.text.stringtemplate;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.regex.RegexpMatcher;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.BiFunction;

import static java.util.regex.Matcher.quoteReplacement;

public class StringTemplate {
    /**
     * index pattern
     */
    public static final Regexp DEFAULT_PATTERN = RegexUtils.createRegexp("\\{\\d+}");
    private static final BiFunction<String, Object[], String> defaultValueGetter = new IndexBasedValueGetter();

    private Regexp variableRegexp = DEFAULT_PATTERN;
    private String template;
    private BiFunction<String, Object[], String> valueGetter = defaultValueGetter;

    public StringTemplate variablePattern(String pattern) {
        if (Emptys.isNotEmpty(pattern)) {
            return variablePattern(RegexUtils.compile(pattern));
        }
        return this;
    }

    public StringTemplate variablePattern(Regexp regexp){
        if (Emptys.isNotNull(regexp)) {
            this.variableRegexp = regexp;
        }
        return this;
    }


    public StringTemplate using(String template) {
        Objects.requireNonNull(template);
        this.template = template;
        return this;
    }

    /**
     * set a value getter
     *
     * @param valueGetter apply(String matched, Object[] args)
     */
    public StringTemplate with(BiFunction<String, Object[], String> valueGetter) {
        if (valueGetter != null) {
            this.valueGetter = valueGetter;
        }
        if (variableRegexp == DEFAULT_PATTERN) {
            this.valueGetter = defaultValueGetter;
        }
        return this;
    }

    public String format(Object[] args) {
        if (Emptys.isNull(args)) {
            args = new Object[0];
        }

        RegexpMatcher matcher = variableRegexp.matcher(this.template);
        StringBuilder b = new StringBuilder();
        Logger logger = Loggers.getLogger(getClass());
        while (matcher.find()) {
            final String matched = matcher.group();
            String value = null;
            try {
                value = valueGetter.apply(matched, args);
            } catch (Exception e) {
               logger.error("parse error",e);
            }
            value = ObjectUtils.isNull(value) ? matched : value;
            matcher.appendReplacement(b,  quoteReplacement(value));
        }
        matcher.appendTail(b);
        return b.toString();
    }


    public static class IndexBasedValueGetter implements BiFunction<String, Object[], String> {
        @Override
        public String apply(String matched, Object[] args) {
            Object object = args[getIndex(matched)];
            return Emptys.isNull(object) ? "" : object.toString();
        }

        private int getIndex(String matched) {
            String indexString = matched;
            if(matched.startsWith("{") && matched.endsWith("}")) {
                indexString = matched.substring(1, matched.length() - 1);
            }
            int index = Integer.parseInt(indexString);
            if (index < 0) {
                index = 0;
            }
            return index;
        }
    }
}
