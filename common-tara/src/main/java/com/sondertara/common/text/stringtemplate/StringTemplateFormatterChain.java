package com.sondertara.common.text.stringtemplate;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.struct.Pair;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.struct.Entry;
import com.sondertara.common.struct.Holder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class StringTemplateFormatterChain implements StringTemplateFormatter {
    private List<Pair<StringTemplateFormatter, Object[]>> chain = new ArrayList<>();

    public StringTemplateFormatterChain addFormatterAndParameters(StringTemplateFormatter formatter, Object... args) {
        if (ObjectUtils.isNotNull(formatter)) {
            chain.add(new Entry<>(formatter, args));
        }
        return this;
    }

    public StringTemplateFormatterChain addFormatterAndParameters(Regexp pattern, BiFunction<String, Object[], String> valueGetter, Object... args) {
        return addFormatterAndParameters(new CustomPatternStringFormatter(pattern, valueGetter), args);
    }

    public StringTemplateFormatterChain addFormatterAndParameters(String variablePattern, BiFunction<String, Object[], String> valueGetter, Object... args) {
        return addFormatterAndParameters(RegexUtils.compile(variablePattern), valueGetter, args);
    }

    public StringTemplateFormatterChain addIndexedFormatterAndParameters(Object... args) {
        return addFormatterAndParameters(new IndexStringFormatter(), args);
    }

    public StringTemplateFormatterChain addPlaceHolderFormatterAndParameters(Object... args) {
        return addFormatterAndParameters(new PlaceholderStringFormatter(), args);
    }

    public <T>StringTemplateFormatterChain addBeanBasedFormatterAndParameters(T bean) {
        return addFormatterAndParameters(new BeanBasedStyleStringFormatter(), bean);
    }

    public StringTemplateFormatterChain addMapBasedFormatterAndParameters(Map<String, Object> map) {
        return addFormatterAndParameters(new MapBasedStringFormatter(), map);
    }

    @Override
    public String format(String template, final Object... args) {
        Objects.requireNonNull(template);
        final Holder<String> templateHolder = new Holder<String>(template);
        CollectionUtils.forEach(chain, new Consumer<Pair<StringTemplateFormatter, Object[]>>() {
            @Override
            public void accept(Pair<StringTemplateFormatter, Object[]> pair) {
                templateHolder.set(pair.getKey().format(templateHolder.get(), pair.getValue()));
            }
        });
        return templateHolder.get();
    }
}
