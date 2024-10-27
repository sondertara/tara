package com.sondertara.common.io.stream;


import com.sondertara.common.base.Valid;
import com.sondertara.common.math.Unsigneds;
import org.jspecify.annotations.NonNull;

import java.io.InputStream;
import java.util.Objects;

public class UnsyncByteArrayInputStream extends InputStream {
    protected byte[] myBuffer;

    private int myPosition;

    private int myCount;

    private int myMarkedPosition;

    public UnsyncByteArrayInputStream(@NonNull byte[] buf) {
        this(buf, 0, buf.length);
    }

    public UnsyncByteArrayInputStream(@NonNull byte[] buf, int offset, int length) {
        init(buf, offset, length);
    }

    public void init(@NonNull byte[] buf, int offset, int length) {
        Objects.requireNonNull(buf);
        this.myBuffer = buf;
        this.myPosition = offset;
        this.myCount = length;
    }

    public int read() {
        return (this.myPosition < this.myCount) ? Unsigneds.toUnsignedByte(this.myBuffer[this.myPosition++]) : -1;
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) {
        Valid.notNull(b);
        if (off < 0 || len < 0 || len > b.length - off)
            throw new IndexOutOfBoundsException();
        if (this.myPosition >= this.myCount)
            return -1;
        if (this.myPosition + len > this.myCount)
            len = this.myCount - this.myPosition;
        if (len <= 0)
            return 0;
        System.arraycopy(this.myBuffer, this.myPosition, b, off, len);
        this.myPosition += len;
        return len;
    }
    @Override
    public long skip(long n) {
        if (this.myPosition + n > this.myCount)
            n = (this.myCount - this.myPosition);
        if (n < 0L)
            return 0L;
        this.myPosition = (int)(this.myPosition + n);
        return n;
    }
    @Override
    public int available() {
        return this.myCount - this.myPosition;
    }
    @Override
    public boolean markSupported() {
        return true;
    }
    @Override
    public synchronized void mark(int readLimit) {
        this.myMarkedPosition = this.myPosition;
    }
    @Override
    public synchronized void reset() {
        this.myPosition = this.myMarkedPosition;
    }
}
