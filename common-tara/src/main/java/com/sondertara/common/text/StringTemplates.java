package com.sondertara.common.text;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.function.ValueGetter;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.text.placeholder.PlaceholderParser;
import com.sondertara.common.text.stringtemplate.BeanBasedStyleStringFormatter;
import com.sondertara.common.text.stringtemplate.CStyleStringFormatter;
import com.sondertara.common.text.stringtemplate.CustomPatternStringFormatter;
import com.sondertara.common.text.stringtemplate.IndexStringFormatter;
import com.sondertara.common.text.stringtemplate.MapBasedStringFormatter;
import com.sondertara.common.text.stringtemplate.PlaceholderStringFormatter;
import com.sondertara.common.text.stringtemplate.StringTemplateFormatter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StringTemplates {

    /**
     * %s, %d
     *
     * @param template the template
     * @param args     the arguments
     * @return formatted string
     * @see String#format(String, Object...)
     */
    public static String formatWithCStyle(String template, Object... args) {
        return new CStyleStringFormatter().format(template, args);
    }

    public static Function<Object[], String> cStyleSupplier(final String template) {
        return new Function<Object[], String>() {
            @Override
            public String apply(Object[] params) {
                return formatWithCStyle(template, params);
            }
        };
    }

    /**
     * format based placeholder: {}
     *
     * @param template the string template
     * @return formatted string
     */
    public static String formatWithPlaceholder(String template, Object... args) {
        return new PlaceholderStringFormatter().format(template, args);
    }

    public static Function<Object[], String> placeholderStyleSupplier(final String template) {
        return new Function<Object[], String>() {
            @Override
            public String apply(Object[] params) {
                return formatWithPlaceholder(template, params);
            }
        };
    }


    /**
     * format based index: {0}, {1}, {2}
     * the index based on 0
     *
     * @param template the string template
     * @return formatted string
     */
    public static String formatWithIndex(String template, Object... args) {
        return new IndexStringFormatter().format(template, args);
    }

    public static Function<Object[], String> indexStyleSupplier(final String template) {
        return params -> formatWithIndex(template, params);
    }

    /**
     * format based on a bean, the variable: ${fieldName}
     *
     * @param template the string template
     * @param bean     the bean
     * @param <T>      the bean type
     * @return formatted string
     */
    public static <T> String formatWithBean(String template, T bean) {
        return new BeanBasedStyleStringFormatter().format(template, bean);
    }

    public static Function<Object[], String> beanStyleSupplier(final String template) {
        return new Function<Object[], String>() {
            @Override
            public String apply(Object[] params) {
                return formatWithBean(template, params);
            }
        };
    }

    /**
     * format based on a map, the variable: ${key}
     *
     * @param template the string template
     * @return formatted string
     */
    public static String formatWithMap(String template, Map<String, ?> map) {
        return formatWithMap(template, MapBasedStringFormatter.PatternStyle.$, map);
    }

    public static Function<Object[], String> mapStyleSupplier(@NonNull final String template) {
        return mapStyleSupplier(template, null);
    }

    @SuppressWarnings({"unchecked"})
    public static Function<Object[], String> mapStyleSupplier(@NonNull final String template, final MapBasedStringFormatter.@Nullable PatternStyle patternStyle) {
        return new Function<Object[], String>() {
            @Override
            public String apply(Object[] params) {
                return formatWithMap(template, patternStyle, Emptys.isNotEmpty(params) ? (Map) params[0] : null);
            }
        };
    }

    /**
     * format based on a map, the variable: ${key}
     *
     * @param template the string template
     * @return formatted string
     */
    public static String formatWithMap(String template, MapBasedStringFormatter.PatternStyle patternStyle, Map<String, ?> map) {
        return new MapBasedStringFormatter(patternStyle).format(template, map);
    }

    /**
     * custom formatter
     *
     * @param template        the string template
     * @param variablePattern variable pattern in template
     * @param valueGetter     variable's value getter, will get value from args
     * @param args            args
     * @return formatted string
     */
    public static String format(String template, String variablePattern, BiFunction<String, Object[], String> valueGetter, Object... args) {
        return new CustomPatternStringFormatter(variablePattern, valueGetter).format(template, args);
    }

    /**
     * custom formatter
     *
     * @param template        the string template
     * @param variablePattern variable pattern in template
     * @param valueGetter     variable's value getter, will get value from args
     * @param args            args
     * @return formatted string
     */
    public static String format(String template, Regexp variablePattern, BiFunction<String, Object[], String> valueGetter, Object... args) {
        // 需要自己剔除变量的前后标记
        return new CustomPatternStringFormatter(variablePattern, valueGetter).format(template, args);
    }

    /**
     * 模板变量替换
     *
     * @param template          模板
     * @param variableStartFlag 变量的regexp pattern的前缀标识，例如 { 或 ${
     * @param variableEndFlag   变量的regexp pattern的后缀标识，例如 { 或 ${
     * @return 替换后排的字符串
     *      */
    public static String format(String template, final String variableStartFlag, final String variableEndFlag, final BiFunction<String, Object[], String> valueGetter, final Object... args) {
        String startFlagPattern = variableStartFlag.replace("$", "\\$")
                .replace("[", "\\[")
                .replace("{", "\\{")
                .replace("(", "\\)");

        String endFlagPattern = variableEndFlag.replace("$", "\\$")
                .replace("]", "\\]")
                .replace("}", "\\}")
                .replace(")", "\\)");

        return format(template, startFlagPattern + "[\\w\\-]+(\\.[\\w\\-]+)*" + endFlagPattern, new BiFunction<String, Object[], String>() {
            @Override
            public String apply(String variable, Object[] arguments) {
                // 需要自己剔除变量的前后标记
                if (variable.startsWith(variableStartFlag)) {
                    variable = variable.substring(variableStartFlag.length());
                }
                if (variable.endsWith(variableEndFlag)) {
                    variable = variable.substring(0, variable.length() - variableEndFlag.length());
                }
                return valueGetter.apply(variable, args);
            }
        });
    }

    /**
     * 模板变量替换
     *
     * @param template              模板
     * @param variablePattern       变量的regexp pattern
     * @param variableValueProvider 用于提供变量值
     * @return 替换后排的字符串
     *      */
    public static String format(String template, Regexp variablePattern, final PlaceholderParser variableValueProvider) {
        return format(template, variablePattern, new BiFunction<String, Object[], String>() {
            @Override
            public String apply(String variable, Object[] arguments) {
                // 需要自己剔除变量的前后标记
                return variableValueProvider.parse(variable);
            }
        });
    }

    /**
     * 模板替换
     *
     * @return 2.10.1
     */
    public static String format(String template, final String variableStartFlag, final String variableEndFlag, final PlaceholderParser variableValueProvider) {
        return format(template, variableStartFlag, variableEndFlag, new BiFunction<String, Object[], String>() {
            @Override
            public String apply(String variable, Object[] arguments) {
                // 需要自己剔除变量的前后标记
                return variableValueProvider.parse(variable);
            }
        });
    }

    /**
     * 模板替换
     *
     * @return 2.10.1
     */
    public static String format(String template, final String variableStartFlag, final String variableEndFlag, final ValueGetter<String> valueGetter) {
        return format(template, variableStartFlag, variableEndFlag, new BiFunction<String, Object[], String>() {
            @Override
            public String apply(String variable, Object[] arguments) {
                // 需要自己剔除变量的前后标记
                return valueGetter.getString(variable);
            }
        });
    }

    public static TemplateFluenter fluenter(String template) {
        return new TemplateFluenter(template);
    }

    public static final class TemplateFluenter {
        private String template;

        private TemplateFluenter(String template) {
            this.template = template;
        }

        public TemplateFluenter format(StringTemplateFormatter formatter, Object... args) {
            template = formatter.format(template, args);
            return this;
        }

        public TemplateFluenter format(String variablePattern, BiFunction<String, Object[], String> valueGetter, Object... args) {
            return format(new CustomPatternStringFormatter(variablePattern, valueGetter), args);
        }

        public TemplateFluenter format(Regexp variablePattern, BiFunction<String, Object[], String> valueGetter, Object... args) {
            return format(new CustomPatternStringFormatter(variablePattern, valueGetter), args);
        }

        public TemplateFluenter formatWithIndex(Object... args) {
            return format(new IndexStringFormatter(), args);
        }

        public TemplateFluenter formatWithPlaceHolder(Object... args) {
            return format(new PlaceholderStringFormatter(), args);
        }

        public <T> TemplateFluenter formatWithBean(T bean) {
            return format(new BeanBasedStyleStringFormatter(), bean);
        }

        public TemplateFluenter formatWithMap(Map<String, Object> map) {
            return format(new MapBasedStringFormatter(), map);
        }

        public String get() {
            return template;
        }
    }

    private StringTemplates() {

    }
}
