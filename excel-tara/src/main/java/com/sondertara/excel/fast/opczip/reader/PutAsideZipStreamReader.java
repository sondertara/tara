package com.sondertara.excel.fast.opczip.reader;

import com.sondertara.common.io.IoUtils;
import com.sondertara.excel.fast.opczip.reader.skipping.ZipStreamReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PutAsideZipStreamReader extends ZipStreamReader {
    private Map<String, byte[]> saved = new HashMap<>();

    public PutAsideZipStreamReader(InputStream in) {
        super(in);
    }

    public void saveStream() throws IOException {
        saved.put(currentEntry.getName(), IoUtils.readBytes(getCompressedStream()));
    }

    public InputStream restoreStream(String name) {
        return uncompressed(new ByteArrayInputStream(saved.get(name)));
    }
}
