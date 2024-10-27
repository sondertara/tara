package com.sondertara.common.io.file.filter;

import java.io.File;

public class WriteableFileFilter implements CommonFileFilter {
    @Override
    public boolean accept(File file) {
        return file.canWrite();
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }
}
