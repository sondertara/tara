package com.sondertara.excel.fast.opczip.reader.skipping;

import java.io.IOException;
import java.io.InputStream;

class ZipReadSpec {

    static final Signature CEN = new Signature(new byte[]{'P', 'K', 1, 2});
    static final Signature LFH = new Signature(new byte[]{'P', 'K', 3, 4});
    static final Signature DAT = new Signature(new byte[]{'P', 'K', 7, 8});

    final static int DATA_DESCRIPTOR_USED = 0x08;
    final static int LFH_SIZE = 30;       // LOC header size
    final static int DAT_SIZE = 16;       // LOC header size
    final static int LFH_VER = 4;        // version needed to extract
    final static int LFH_FLG = 6;        // general purpose bit flag
    final static int LFH_HOW = 8;        // compression method
    final static int LFH_TIM = 10;       // modification time
    final static int LFH_CRC = 14;       // uncompressed file crc-32 value
    final static int LFH_SIZ = 18;       // compressed size
    final static int LFH_LEN = 22;       // uncompressed size
    final static int LFH_NAM = 26;       // filename length
    final static int LFH_EXT = 28;       // extra field length

    /**
     * Fetches unsigned 16-bit value from byte array at specified offset.
     * The bytes are assumed to be in Intel (little-endian) byte order.
     */
    static int get16(byte[] b, int off) {
        return (b[off] & 0xff) | ((b[off + 1] & 0xff) << 8);
    }

    /**
     * Fetches unsigned 32-bit value from byte array at specified offset.
     * The bytes are assumed to be in Intel (little-endian) byte order.
     */
    static long get32(byte[] b, int off) {
        return (get16(b, off) | ((long) get16(b, off + 2) << 16)) & 0xffffffffL;
    }

    static byte[] readNBytes(InputStream in, int len) throws IOException {
        byte[] buf = new byte[len];
        int read = ExactIO.readExactly(in, buf, 0, len);
        if (read != len) {
            throw new IOException("unexpected EOF");
        }
        return buf;
    }

}
