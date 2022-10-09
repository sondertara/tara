package com.sondertara.common.io;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class CharacterReader {

    private static final int BUFFER_SIZE = 1024;

    private final Reader reader;

    private final char[] buffer;

    private int pos;

    private int size;

    public CharacterReader(String reader) {
        this.reader = new StringReader(reader);
        buffer = new char[BUFFER_SIZE];
    }

    /**
     * Returns the character at the pos subscript and returns
     *
     * @return Char
     */
    public char peek() {
        if (pos - 1 >= size) {
            return (char) -1;
        }

        return buffer[Math.max(0, pos - 1)];
    }

    public char peek(int c) {
        if (pos - c > size - 1) {
            return (char) -1;
        }

        return buffer[Math.max(0, pos - c)];
    }

    public char prep() {
        if (pos <= 0) {
            return buffer[0];
        }
        return buffer[Math.min(pos + 1, pos)];
    }

    /**
     * Returns the character at the pos subscript, and pos + 1, and finally returns
     * the character
     *
     * @throws IOException IOException
     * @return Char
     */
    public char next() throws IOException {
        if (!hasMore()) {
            return (char) -1;
        }

        return buffer[pos++];
    }

    /**
     * If it is not the last element, move the subscript forward one bit
     */
    public void back() {
        pos = Math.max(0, --pos);
    }

    /**
     * Determine if there are more elements
     *
     * @throws IOException
     * @return true-hasmore | false-not hasmore
     */
    public boolean hasMore() throws IOException {
        if (pos < size) {
            return true;
        }

        fillBuffer();
        return pos < size;
    }

    /**
     * Reset subscript and number of elements
     *
     * @throws IOException IOException
     */
    private void fillBuffer() throws IOException {
        int n = reader.read(buffer);
        if (n == -1) {
            return;
        }

        pos = 0;
        size = n;
    }

}