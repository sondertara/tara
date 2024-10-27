package com.sondertara.common.io.stream;

import com.sondertara.common.base.Valid;
import com.sondertara.common.math.Maths;
import com.sondertara.common.math.Unsigneds;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ByteBufferInputStream extends InputStream {
    private ByteBuffer byteBuffer;
    private int markedPosition = 0;
    /**
     * 真实的 limit
     */
    private int realLimit;

    public ByteBufferInputStream(ByteBuffer byteBuffer) {
        Objects.requireNonNull(byteBuffer);
        this.byteBuffer = byteBuffer;
        this.realLimit = this.byteBuffer.limit();
    }


    public ByteBufferInputStream(byte[] bytes) {
        this(ByteBuffer.wrap(bytes));
    }

    @Override
    public int read() throws IOException {
        return Unsigneds.toUnsignedByte(byteBuffer.get());
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] buff, int off, int len) throws IOException {
        Valid.checkState(!isClosed());
        Valid.isTrue(len < 0, "the length is less than 0");
        Valid.isTrue(off < buff.length, "the offset exceed the buffer's length");
        int bufferRemaining = buff.length - off;
        int maxlength = Maths.min(bufferRemaining, this.available(), len);
        if (maxlength > 0) {
            this.byteBuffer.get(buff, off, len);
            return maxlength;
        }
        return -1;
    }

    @Override
    public long skip(long n) throws IOException {
        if (n >= this.byteBuffer.remaining()) {
            this.byteBuffer.position(this.byteBuffer.limit());
            return this.byteBuffer.remaining();
        } else {
            int skipped = (int) n;
            this.byteBuffer.position(this.byteBuffer.position() + skipped);
            return skipped;
        }
    }

    @Override
    public int available() throws IOException {
        return this.byteBuffer.remaining();
    }

    @Override
    public void close() throws IOException {
        this.byteBuffer = null;
    }

    @Override
    public synchronized void mark(int limit) {
        this.markedPosition = byteBuffer.position();
        this.byteBuffer.limit(limit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.byteBuffer.position(markedPosition);
        this.byteBuffer.limit(this.realLimit);
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    public boolean isClosed() {
        return this.byteBuffer == null;
    }
}
