package com.sondertara.common.io.file.filter;


import com.sondertara.common.function.predicate.StringEqualsPredicate;

/**
 *  */
public class FilenameEqualsFilter extends FilenamePredicateFilter {


    public FilenameEqualsFilter(String ref) {
        this(ref, false);
    }

    public FilenameEqualsFilter(String ref, boolean ignoreCase) {
        super(new StringEqualsPredicate(ref, ignoreCase));
    }

}
