package com.sondertara.common.io.file.filter;

import java.io.File;

public class ExistsFileFilter implements CommonFileFilter {
    @Override
    public boolean accept(File file) {
        return file.exists();
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }
}
