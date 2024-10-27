package com.sondertara.common.function.predicate;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.StreamUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StringStartsWithPredicate implements Predicate<String> {
    private boolean ignoreCase;
    private Set<String> prefixes = new HashSet<String>();

    public StringStartsWithPredicate(String... prefixes) {
        this(true, prefixes);
    }

    public StringStartsWithPredicate(boolean ignoreCase, String... prefixes) {
        this(ignoreCase, Lists.asList(prefixes));
    }

    public StringStartsWithPredicate(List<String> prefixes) {
        this(true, prefixes);
    }

    public StringStartsWithPredicate(boolean ignoreCase, Iterable<String> prefixes) {
        if (ignoreCase) {
            for (String suffix : prefixes) {
                if (StringUtils.isNotBlank(suffix)) {
                    this.prefixes.add(suffix.toLowerCase());
                }
            }
        }
        this.prefixes.addAll(StreamUtils.of(prefixes).filter(Objects::nonNull).collect(Collectors.toSet()));
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean test(final String value) {
        Valid.isTrue(CollectionUtils.isNotEmpty(this.prefixes));
        return CollectionUtils.anyMatch(this.prefixes, new Predicate<String>() {
            @Override
            public boolean test(String prefix) {
                return StringUtils.startsWith(value, prefix, ignoreCase);
            }
        });
    }
}
