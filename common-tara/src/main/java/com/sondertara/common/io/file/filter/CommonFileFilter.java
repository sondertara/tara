package com.sondertara.common.io.file.filter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.function.Predicate;

/**
 * @author huangxiaohu.1ih
 */
public interface CommonFileFilter extends FileFilter, FilenameFilter, Predicate<File> {
    @Override
    default boolean test(File file) {
        return accept(file);
    }
}
