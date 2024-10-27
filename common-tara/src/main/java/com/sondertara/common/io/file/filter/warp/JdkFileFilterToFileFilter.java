package com.sondertara.common.io.file.filter.warp;

import com.sondertara.common.io.file.filter.CommonFileFilter;

import java.io.File;
import java.io.FileFilter;
/**
 *  */
public class JdkFileFilterToFileFilter implements CommonFileFilter {
    private FileFilter delegate;

    public JdkFileFilterToFileFilter(FileFilter delegate){
        this.delegate = delegate;
    }

    @Override
    public boolean accept(File e) {
        return delegate.accept(e);
    }

    @Override
    public boolean accept(File dir, String name) {
        return delegate.accept(new File(dir,name));
    }
}
