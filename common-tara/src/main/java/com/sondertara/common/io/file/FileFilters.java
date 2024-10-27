package com.sondertara.common.io.file;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.Emptys;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.io.file.filter.CommonFileFilter;
import com.sondertara.common.io.file.filter.warp.FilenameFilterToFileFilter;
import com.sondertara.common.io.file.filter.warp.FilterToFileFilter;
import com.sondertara.common.io.file.filter.warp.JdkFileFilterToFileFilter;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.function.Predicate;

public class FileFilters {
    private FileFilters() {

    }

    public static FileFilter allFileFilter(@NonNull FileFilter... predicates) {
        return allFileFilter(Lists.newArrayList(predicates));
    }

    public static FileFilter allFileFilter(@NonNull List<? extends FileFilter> predicates) {
        Assert.isTrue(Emptys.isNotEmpty(predicates));
        return new CommonFileFilter() {
            @Override
            public boolean accept(final File e) {
                return predicates.stream().allMatch(new Predicate<FileFilter>() {
                    @Override
                    public boolean test(FileFilter fileFilter) {
                        return fileFilter.accept(e);
                    }
                });
            }

            @Override
            public boolean accept(File dir, String name) {
                return accept(new File(dir, name));
            }

        };
    }


    public static FileFilter anyFileFilter(@NonNull FileFilter... predicates) {
        return anyFileFilter(Lists.newArrayList(predicates));
    }

    public static FileFilter anyFileFilter(@NonNull List<? extends FileFilter> predicates) {
        Assert.isTrue(Emptys.isNotEmpty(predicates));
        return new CommonFileFilter() {
            @Override
            public boolean accept(final File e) {
                return predicates.stream().anyMatch(new Predicate<FileFilter>() {
                    @Override
                    public boolean test(FileFilter fileFilter) {
                        return fileFilter.accept(e);
                    }
                });
            }

            @Override
            public boolean accept(File dir, String name) {
                return accept(new File(dir, name));
            }
        };
    }


    /**
     *
     */
    public static FileFilter wrap(FilenameFilter filter) {
        return new FilenameFilterToFileFilter(filter);
    }

    /**
     *
     */
    public static FileFilter wrap(java.io.FilenameFilter filter) {
        return new FilenameFilterToFileFilter(filter);
    }

    /**
     *
     */
    public static FileFilter wrap(Predicate<File> filter) {
        return new FilterToFileFilter(filter);
    }

    /**
     *
     */
    public static FileFilter wrap(java.io.FileFilter fileFilter) {
        return new JdkFileFilterToFileFilter(fileFilter);
    }
}
