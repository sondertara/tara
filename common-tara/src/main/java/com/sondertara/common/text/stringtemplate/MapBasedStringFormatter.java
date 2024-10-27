package com.sondertara.common.text.stringtemplate;

import com.sondertara.common.accessor.Accessor;
import com.sondertara.common.collection.MapAccessor;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class MapBasedStringFormatter extends CustomPatternStringFormatter {
    public enum PatternStyle {
        $(RegexUtils.compile("\\$\\{\\w+(\\.\\w+)*}")),
        PLACE_HOLDER(RegexUtils.compile("\\{\\w+(\\.\\w+)*}"));

        private Regexp pattern;

        PatternStyle(Regexp pattern) {
            this.pattern = pattern;
        }

        public Regexp getPattern() {
            return pattern;
        }
    }

    public MapBasedStringFormatter() {
        this(PatternStyle.$);
    }

    public MapBasedStringFormatter(@Nullable PatternStyle patternStyle) {
        super((patternStyle == null ? PatternStyle.$ : patternStyle).getPattern(), new MapValueGetter(patternStyle));
    }


    private static class MapValueGetter implements BiFunction<String, Object[], String> {
        private PatternStyle style;

        MapValueGetter(PatternStyle style) {
            this.style = style;
        }

        @Override
        @SuppressWarnings("unchecked")
        public String apply(String matched, Object[] args) {
            Objects.requireNonNull(args);
            matched = style == PatternStyle.$ ? matched.substring(2, matched.length() - 1) : matched.substring(1, matched.length() - 1);
            Accessor<String, ?> accessor = new MapAccessor((Map) args[0]);
            return accessor.getString(matched);
        }
    }
}
