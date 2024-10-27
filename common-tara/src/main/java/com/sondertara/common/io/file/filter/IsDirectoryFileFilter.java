package com.sondertara.common.io.file.filter;

import java.io.File;

public class IsDirectoryFileFilter implements CommonFileFilter {

    @Override
    public boolean accept(File dir, String filename) {
        return accept(new File(dir, filename));
    }

    @Override
    public boolean accept(File file) {
        return file.isDirectory();
    }
}
