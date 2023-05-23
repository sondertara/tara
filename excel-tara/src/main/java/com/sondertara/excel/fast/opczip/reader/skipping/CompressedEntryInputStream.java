package com.sondertara.excel.fast.opczip.reader.skipping;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

import static com.sondertara.excel.fast.opczip.reader.skipping.ZipReadSpec.*;


class CompressedEntryInputStream extends FilterInputStream {
    private final SignatureMatcher cen = new SignatureMatcher(CEN);
    private final SignatureMatcher lfh = new SignatureMatcher(LFH);
    private final SignatureMatcher dat = new SignatureMatcher(DAT);

    private final ZipEntry entry;
    private final boolean expectingDatSig;
    private boolean endOfEntry = false;
    int count = 0;
    CRC32 crc32 = new CRC32();

    public CompressedEntryInputStream(PushbackInputStream in, ZipEntry entry, boolean expectingDatSig) {
        super(in);
        this.entry = entry;
        this.expectingDatSig = expectingDatSig;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int read = read(b, 0, 1);
        return read == 1 ? b[0] : read;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (endOfEntry) {
            return -1;
        }
        if (entry.getCompressedSize() > 0) {
            long leftToRead = entry.getCompressedSize() - count;
            if(leftToRead <= 0){
                return -1;
            }
            int readCount = super.read(buf, off, (int) Math.min(len, leftToRead));
            if (readCount > 0) {
                count += readCount;
                crc(buf, off, readCount);
            }
            return readCount;
        } else {
            int readCount = super.read(buf, off, len);
            crc(buf, off, readCount);
            for (int i = 0; i < len; i++) {
                byte currentByte = buf[off + i];
                if (expectingDatSig && dat.matchNext(currentByte)) {
                    eof(buf, off, len, i - DAT.length() + 1);
                    ExactIO.skipExactly(in, DAT_SIZE);
                    return i - DAT.length() + 1;
                }
                if (lfh.matchNext(currentByte) || cen.matchNext(currentByte)) {
                    eof(buf, off, len, i - LFH.length() + 1);
                    return i - LFH.length() + 1;
                }
            }
            return readCount;
        }
    }

    private void crc(byte[] buf, int off, int readCount) {
        if (entry.getCrc() > 0) {
            crc32.update(buf, off, readCount);
        }
    }

    private void eof(byte[] buf, int off, int len, int i) throws IOException {
        ((PushbackInputStream) in).unread(buf, off + i, len - i);
        endOfEntry = true;
        if (entry.getCrc() != -1 && crc32.getValue() != entry.getCrc()) {
            throw new IOException("CRC32: Expecting " + entry.getCrc() + ". Got: " + crc32.getValue());
        }
        if (entry.getCompressedSize() != -1 && count != entry.getCompressedSize()) {
            throw new IOException("Expecting compressed size " + entry.getCompressedSize() + ". Got: " + count);
        }
    }

}
