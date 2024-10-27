package com.sondertara.common.io.file.filter.warp;

import com.sondertara.common.io.file.filter.CommonFileFilter;

import java.io.File;
import java.util.function.Predicate;

/**
 *  */
public class FilePredicateToFilterFilter implements CommonFileFilter {
    private Predicate<File> predicate;

    public FilePredicateToFilterFilter(Predicate<File> filePredicate){
        this.predicate = filePredicate;
    }

    @Override
    public boolean accept(File e) {
        return predicate.test(e);
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir,name));
    }
}
