package com.sondertara.excel.fast.opczip.reader.skipping;

import java.io.IOException;
import java.io.InputStream;

class ExactIO {
    static int readExactly(InputStream in, byte[] buf, int offset, int len) throws IOException {
        // in Java9:
        // return in.readAllBytes();
        int n = 0;
        while (n < len) {
            int count = in.read(buf, offset + n, len - n);
            if (count < 0) {
                break;
            }
            n += count;
        }
        return n;

    }

    static void skipExactly(InputStream in, long bytes) throws IOException {
        long leftToSkip = bytes;
        while (leftToSkip > 0) {
            leftToSkip -= in.skip(leftToSkip);
        }
    }

}
