package com.sondertara.excel.fast.opczip.reader.ordered;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class MemCacheOrderedZipStreamReader extends OrderedZipStreamReader {
    private Map<String, byte[]> cache = new HashMap<>();

    @Override
    protected OutputStream getTempOutputStream(String name) {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                cache.put(name, this.toByteArray());
            }
        };
    }

    @Override
    protected InputStream getTempInputStream(String name) throws UncheckedIOException {
        byte[] buf = cache.get(name);
        if (buf == null) {
            throw new IllegalStateException("No cache for " + name);
        }
        return new ByteArrayInputStream(buf);
    }
}

