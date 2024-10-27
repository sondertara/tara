package com.sondertara.common.io.file.filter;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.function.predicate.StringEndsWithPredicate;
import com.sondertara.common.io.file.Filenames;

import java.util.List;

public class FilenameSuffixFilter extends FilenamePredicateFilter {

    public FilenameSuffixFilter(String suffix) {
        this(suffix, true);
    }

    public FilenameSuffixFilter(String suffix, boolean ignoreCase) {
        super(new StringEndsWithPredicate(ignoreCase, suffix));
    }

    public FilenameSuffixFilter(String[] suffixes) {
        this(suffixes, true);
    }

    public FilenameSuffixFilter(String[] suffixes, boolean ignoreCase) {
        this(Lists.asList(suffixes), ignoreCase);
    }

    public FilenameSuffixFilter(List<String> suffixes) {
        this(suffixes, true);
    }

    public FilenameSuffixFilter(List<String> suffixes, boolean ignoreCase) {
        super(new StringEndsWithPredicate(ignoreCase, suffixes));
    }

    @Override
    protected boolean doTest(String name) {
        String suffix = Filenames.getSuffix(name);
        return super.doTest(suffix);
    }
}
