package com.sondertara.common.io.file.filter;

import java.io.File;
import java.util.List;

public class ParentFilenameSuffixFilter implements CommonFileFilter {
    private FilenameSuffixFilter filenameSuffixFilter;

    public ParentFilenameSuffixFilter(boolean ignoreCase, String... suffixes) {
        this.filenameSuffixFilter = new FilenameSuffixFilter(suffixes, ignoreCase);
    }

    public ParentFilenameSuffixFilter(List<String> suffixes, boolean ignoreCase) {
        this.filenameSuffixFilter = new FilenameSuffixFilter(suffixes, ignoreCase);
    }

    @Override
    public boolean accept(File e) {
        return filenameSuffixFilter.accept(e.getParentFile());
    }

    @Override
    public boolean accept(File dir, String name) {
        return filenameSuffixFilter.accept(dir);
    }
}
