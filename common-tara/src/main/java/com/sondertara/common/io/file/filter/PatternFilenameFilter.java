package com.sondertara.common.io.file.filter;

import com.sondertara.common.function.predicate.StringPatternPredicate;
import org.jspecify.annotations.NonNull;

import java.util.regex.Pattern;

public class PatternFilenameFilter extends FilenamePredicateFilter {

    public PatternFilenameFilter(@NonNull String pattern) {
        super(new StringPatternPredicate(pattern));
    }

    public PatternFilenameFilter(@NonNull Pattern pattern) {
        super(new StringPatternPredicate(pattern));
    }
}
