package com.sondertara.common.security.prevention.injection;


import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.function.Predicate2;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.regex.RegexpMatcher;
import com.sondertara.common.struct.Holder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.regex.Matcher.quoteReplacement;

public class LogInjectionPreventionHandler implements Function<String, String> {
    private Map<Regexp, String> replacementMapping = new HashMap<Regexp, String>();

    public void addReplacement(Regexp pattern, String replacement) {
        replacementMapping.put(pattern, replacement);
    }

    public void addCLRFReplacement(String replacement) {
        replacement = StringUtils.isEmpty(replacement) ? "_" : replacement;
        String pattern = "[\r\n\f]";
        addReplacement(RegexUtils.createRegexp(pattern), replacement);
    }

    @Override
    public String apply(String input) {
        if (input == null) {
            return null;
        }
        final Holder<String> stringHolder = new Holder<String>(input);
        CollectionUtils.forEach(replacementMapping, new BiConsumer<Regexp, String>() {
            @Override
            public void accept(Regexp pattern, String replacement) {
                String v = stringHolder.get();
                RegexpMatcher matcher = pattern.matcher(v);
                StringBuilder b = new StringBuilder();
                while (matcher.find()) {
                    matcher.appendReplacement(b, quoteReplacement(replacement));
                }
                matcher.appendTail(b);
                v = b.toString();
                stringHolder.set(v);
            }
        }, new Predicate2<Regexp, String>() {
            @Override
            public boolean test(Regexp pattern, String str) {
                return stringHolder.isEmpty();
            }
        });
        return stringHolder.get();
    }
}
