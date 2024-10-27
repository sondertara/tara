package com.sondertara.common.io.file.filter;

import com.sondertara.common.io.file.Filenames;

import java.io.File;

public class ValidFilenameFilter implements CommonFileFilter {
    @Override
    public boolean accept(File file) {
        return Filenames.checkFilePath(file.getPath());
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }
}
