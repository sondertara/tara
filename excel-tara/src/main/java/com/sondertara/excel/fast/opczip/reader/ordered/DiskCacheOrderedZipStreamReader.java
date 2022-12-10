package com.sondertara.excel.fast.opczip.reader.ordered;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;

public class DiskCacheOrderedZipStreamReader extends OrderedZipStreamReader {
    private final File dir;

    public DiskCacheOrderedZipStreamReader() throws IOException {
        dir = Files.createTempDirectory(DiskCacheOrderedZipStreamReader.class.getSimpleName()).toFile();
    }

    @Override
    protected OutputStream getTempOutputStream(String name) throws FileNotFoundException {
        File file = new File(dir, name);
        file.deleteOnExit();
        return new FileOutputStream(file);
    }

    @Override
    protected InputStream getTempInputStream(String name) throws UncheckedIOException {
        try {
            File file = new File(dir, name);
            return new FileInputStream(file) {
                @Override
                public void close() throws IOException {
                    super.close();
                    file.delete();
                }
            };
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
}

