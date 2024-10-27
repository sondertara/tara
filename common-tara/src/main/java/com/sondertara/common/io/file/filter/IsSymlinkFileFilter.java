package com.sondertara.common.io.file.filter;

import com.sondertara.common.io.file.FileSystems;

import java.io.File;

public class IsSymlinkFileFilter implements CommonFileFilter {
    @Override
    public boolean accept(File file) {
        return FileSystems.isSymlink(file);
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }
}
