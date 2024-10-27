package com.sondertara.common.function.predicate;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.StreamUtils;

import java.util.Collection;
import java.util.function.Predicate;

public class StringContainsAnyPredicate implements Predicate<String> {
    private Collection<String> seeds;
    private boolean ignoreCase;

    public StringContainsAnyPredicate(String... seeds) {
        this(true, Lists.asList(seeds));
    }

    public StringContainsAnyPredicate(Collection<String> seeds) {
        this(true, seeds);
    }

    public StringContainsAnyPredicate(boolean ignoreCase, Collection<String> seeds) {
        this.seeds = seeds;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean test(final String value) {
        return StreamUtils.of(seeds)
                .anyMatch(new Predicate<String>() {
                    @Override
                    public boolean test(String seed) {
                        return StringUtils.contains(value, seed, ignoreCase);
                    }
                });
    }
}
