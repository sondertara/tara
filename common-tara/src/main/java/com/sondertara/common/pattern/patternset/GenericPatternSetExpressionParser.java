package com.sondertara.common.pattern.patternset;


import com.sondertara.common.base.Assert;
import com.sondertara.common.base.Named;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.exception.ExpressionParseException;
import com.sondertara.common.function.Factory;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class GenericPatternSetExpressionParser<PatternEntry extends Named> implements PatternSetExpressionParser<PatternEntry> {

    @NonNull
    private Factory<String, PatternEntry> patternFactory;

    public GenericPatternSetExpressionParser(Factory<String, PatternEntry> patternFactory) {
        setPatternFactory(patternFactory);
    }

    @Override
    public PatternSet<PatternEntry> parse(String expression) {
        Assert.notEmpty(expression, "the expression is empty or null");
        Assert.notEmpty(getSeparator(), "the separator is empty or null");
        Assert.notNull(patternFactory, "the pattern factory is null");

        List<String> segments = Lists.asList(StringUtils.split(expression, getSeparator()));

        PatternSet<PatternEntry> patternSet = internalParse(segments);

        patternSet.setExcludeFlag(getExcludeFlag());
        patternSet.setSeparator(getSeparator());
        patternSet.setExpression(expression);

        return patternSet;
    }

    protected PatternSet<PatternEntry> internalParse(List<String> segments) throws ExpressionParseException {
        final PatternSet<PatternEntry> patternSet = new PatternSet<PatternEntry>();
        CollectionUtils.forEach(segments, new Consumer<String>() {
            @Override
            public void accept(String segment) {
                if (StringUtils.startsWith(segment, getExcludeFlag())) {
                    patternSet.addExclude(patternFactory.get(StringUtils.substring(segment, getExcludeFlag().length())));
                } else {
                    patternSet.addInclude(patternFactory.get(segment));
                }
            }
        });
        return patternSet;
    }

    @Override
    public String getSeparator() {
        return ";";
    }

    @Override
    public String getExcludeFlag() {
        return "!";
    }

    public Factory<String, PatternEntry> getPatternFactory() {
        return patternFactory;
    }

    public void setPatternFactory(Factory<String, PatternEntry> patternFactory) {
        this.patternFactory = patternFactory;
    }
}
