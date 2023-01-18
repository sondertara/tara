package com.sondertara.excel.fast.opczip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class OpcZipOutputStream extends ZipOutputStream {
    private final OpcOutputStream out;

    public OpcZipOutputStream(OutputStream out) {
        super(out);
        this.out = new OpcOutputStream(out);
    }

    @Override
    public void setLevel(int level) {
        out.setLevel(level);
    }

    @Override
    public void putNextEntry(ZipEntry e) throws IOException {
        out.putNextEntry(e);
    }

    @Override
    public void closeEntry() throws IOException {
        out.closeEntry();
    }

    @Override
    public void finish() throws IOException {
        out.finish();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }
}
