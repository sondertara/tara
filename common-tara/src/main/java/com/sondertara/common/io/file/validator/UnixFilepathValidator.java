package com.sondertara.common.io.file.validator;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.io.file.OsFileSystem;

import java.util.List;
import java.util.function.Predicate;

public class UnixFilepathValidator extends AbstractFilepathValidator {

    @Override
    public boolean isLegalFilename(String name) {
        return OsFileSystem.LINUX.isLegalFileName(name);
    }

    @Override
    public boolean isLegalFilepath(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }

        while (StringUtils.startsWith(path, "/")) {
            path = StringUtils.substring(path, 1);
        }

        List<String> segments = StringUtils.split(path, '/', false, false);
        return CollectionUtils.allMatch((Predicate<String>) this::isLegalFilename, segments);
    }

    static final UnixFilepathValidator INSTANCE = new UnixFilepathValidator();
}
