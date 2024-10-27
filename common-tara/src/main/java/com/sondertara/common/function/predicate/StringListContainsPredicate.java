package com.sondertara.common.function.predicate;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.Lists;

import java.util.Collection;
import java.util.Comparator;

public class StringListContainsPredicate extends ContainsPredicate<String> {
    public StringListContainsPredicate(String... array) {
        this(true, array);
    }

    public StringListContainsPredicate(boolean ignoreCase, String... array) {
        this(ignoreCase, Lists.asList(array));
    }

    public StringListContainsPredicate(Collection<String> collection) {
        this(true, collection);
    }

    public StringListContainsPredicate(final boolean ignoreCase, Collection<String> collection) {
        super(collection, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return StringUtils.equals(o1, o2, ignoreCase) ? 0 : o1.compareTo(o2);
            }
        });
    }

}
