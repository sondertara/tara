package com.sondertara.common.function.predicate;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StringEndsWithPredicate implements Predicate<String> {

    private boolean ignoreCase;
    private Set<String> suffixes = new HashSet<String>();

    public StringEndsWithPredicate(String... suffixes) {
        this(true, suffixes);
    }

    public StringEndsWithPredicate(boolean ignoreCase, String... suffixes) {
        this(ignoreCase, Lists.asList(suffixes));
    }

    public StringEndsWithPredicate(List<String> suffixes) {
        this(true, suffixes);
    }

    public StringEndsWithPredicate(boolean ignoreCase, List<String> suffixes) {
        if (ignoreCase) {
            for (String suffix : suffixes) {
                if (StringUtils.isNotBlank(suffix)) {
                    this.suffixes.add(suffix.toLowerCase());
                }
            }
        }
        this.suffixes.addAll( suffixes.stream().filter(Objects::nonNull).collect(Collectors.toSet()));
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean test(final String value) {
        Valid.isTrue(CollectionUtils.isNotEmpty(this.suffixes));
        return CollectionUtils.anyMatch(this.suffixes, new Predicate<String>() {
            @Override
            public boolean test(String suffix) {
                return StringUtils.endsWith(value, suffix, ignoreCase);
            }
        });
    }
}
