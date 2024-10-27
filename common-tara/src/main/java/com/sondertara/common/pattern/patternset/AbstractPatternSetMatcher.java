package com.sondertara.common.pattern.patternset;


import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.Named;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.pattern.AbstractPatternMatcher;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public abstract class AbstractPatternSetMatcher<PatternEntry extends Named> extends AbstractPatternMatcher {
    @Nullable
    private PatternSet<PatternEntry> defaultPatternSet;
    /**
     * 只在使用 #setDefaultExpression, #setPatternExpression 时，要求该字段不能为null
     */
    @Nullable
    private PatternSetExpressionParser<PatternEntry> expressionParser;
    @Nullable
    private PatternSet<PatternEntry> patternSet;

    protected AbstractPatternSetMatcher(@Nullable PatternSetExpressionParser<PatternEntry> expressionParser, @Nullable String defaultPatternExpression) {
        setExpressionParser(expressionParser);
        setDefaultExpression(defaultPatternExpression);
    }

    protected AbstractPatternSetMatcher(@NonNull PatternSetExpressionParser<PatternEntry> expressionParser, @Nullable PatternSet<PatternEntry> defaultPatternSet) {
        setExpressionParser(expressionParser);
        setDefaultPatternSet(defaultPatternSet);
    }

    protected AbstractPatternSetMatcher(@NonNull PatternSetExpressionParser<PatternEntry> expressionParser, @Nullable PatternSet<PatternEntry> defaultPatternSet, PatternSet<PatternEntry> patternSet) {
        setExpressionParser(expressionParser);
        setDefaultPatternSet(defaultPatternSet);
        setPatternSet(patternSet);
    }

    protected AbstractPatternSetMatcher(@NonNull PatternSetExpressionParser<PatternEntry> expressionParser, @Nullable String defaultPatternSet, String patternSetExpression) {
        setExpressionParser(expressionParser);
        setDefaultExpression(defaultPatternSet);
        setPatternExpression(patternSetExpression);
    }

    public void setExpressionParser(PatternSetExpressionParser<PatternEntry> expressionParser) {
        Valid.notNull(expressionParser);
        this.expressionParser = expressionParser;
    }

    public PatternSet<PatternEntry> getDefaultPatternSet() {
        return defaultPatternSet;
    }

    public void setDefaultPatternSet(@Nullable PatternSet<PatternEntry> defaultPatternSet) {
        this.defaultPatternSet = defaultPatternSet;
    }

    public void setDefaultExpression(@Nullable String defaultExpression) {
        if (StringUtils.isNotEmpty(defaultExpression)) {
            Valid.notNull(expressionParser, "the expression parser is null");
            setDefaultPatternSet(expressionParser.parse(defaultExpression));
        }
    }

    public void setPatternSet(PatternSet<PatternEntry> patternSet) {
        this.patternSet = patternSet;
    }

    public void setPatternExpression(@Nullable String expression) {
        if (StringUtils.isNotEmpty(expression)) {
            Valid.notNull(expressionParser, "the expression parser is null");
            setPatternSet(expressionParser.parse(expression));
        }
    }

    @Override
    public Boolean matches(final String string) {
        PatternSet<PatternEntry> patternSet = defaultPatternSet;
        if (Emptys.isNotEmpty(this.patternSet)) {
            patternSet = this.patternSet;
        }

        if (Emptys.isEmpty(patternSet)) {
            throw new IllegalStateException("has no any pattern");
        }

        boolean matched = CollectionUtils.anyMatch(patternSet.getIncludes(), new Predicate<PatternEntry>() {
            @Override
            public boolean test(PatternEntry patternEntry) {
                return doMatch(patternEntry.getName(), string, option.isGlobal());
            }
        });

        if (matched) {
            matched = CollectionUtils.noneMatch(patternSet.getExcludes(), new Predicate<PatternEntry>() {
                @Override
                public boolean test(PatternEntry patternEntry) {
                    return doMatch(patternEntry.getName(), string, option.isGlobal());
                }
            });
        }

        return matched;
    }

    protected abstract boolean doMatch(String pattern, String string, boolean fullMatch);
}
