package com.sondertara.common.io.file.filter;

import com.sondertara.common.io.file.FileSystems;

import java.io.File;

public class IsHiddenFileFilter implements CommonFileFilter {
    @Override
    public boolean accept(File file) {
        return file.isHidden() || FileSystems.isHidden(file);
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }
}
