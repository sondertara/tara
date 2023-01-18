package com.sondertara.excel.fast.opczip.reader.skipping;

import java.util.Arrays;

class Signature {
    final byte[] bytes;

    Signature(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean matchesStartOf(byte[] buf) {
        return Arrays.equals(
                Arrays.copyOf(buf, bytes.length),
                bytes
        );
        // in Java9:
        // return Arrays.equals(buf, 0, bytes.length, bytes, 0, bytes.length);
    }

    public int length() {
        return bytes.length;
    }

    @Override
    public String toString() {
        return toString(bytes, bytes.length);
    }

    public static String toString(byte[] buf, int len) {
        StringBuilder s = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            byte b = buf[i];
            char c = (char) b;
            if (Character.isISOControl(c)) {
                s.append('\\').append(String.format("%02d", (short) c));
            } else {
                s.append(c);
            }
        }
        return s.toString();
    }

    public byte at(int index) {
        return bytes[index];
    }
}
