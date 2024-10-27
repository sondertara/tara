package com.sondertara.common.io.file.filter.warp;

import com.sondertara.common.io.file.filter.CommonFileFilter;

import java.io.File;
import java.util.function.Predicate;

/**
 *  */
public class FilterToFileFilter implements CommonFileFilter {
    private Predicate<File> delegate;

    public FilterToFileFilter(Predicate<File> filter){
        this.delegate= filter;
    }

    @Override
    public boolean accept(File e) {
        return this.delegate.test(e);
    }

    @Override
    public boolean accept(File dir, String name) {
        return this.delegate.test(new File(dir,name));
    }
}
