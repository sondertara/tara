package com.sondertara.common.io.file.filter;

import java.io.File;
import java.util.function.Predicate;

public class FilenamePredicateFilter implements CommonFileFilter {
    protected Predicate<String> predicate;

    public FilenamePredicateFilter(Predicate<String> predicate){
        this.predicate = predicate;
    }

    @Override
    public final boolean accept(File e) {
        return accept(e.getParentFile(), e.getName());
    }

    @Override
    public boolean accept(File dir, String name) {
        return doTest(name);
    }

    protected boolean doTest(String name){
        return predicate.test(name);
    }
}
