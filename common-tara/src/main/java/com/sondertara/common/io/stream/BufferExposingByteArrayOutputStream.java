package com.sondertara.common.io.stream;


import com.sondertara.common.base.Assert;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public final class BufferExposingByteArrayOutputStream extends UnsyncByteArrayOutputStream {
    public BufferExposingByteArrayOutputStream() {
    }

    public BufferExposingByteArrayOutputStream(int size) {
        super(size);
    }

    public BufferExposingByteArrayOutputStream(byte[] buffer) {
        super(buffer);
    }

    public BufferExposingByteArrayOutputStream(@NonNull ByteArrayAllocator allocator, int initialSize) {
        super(allocator, initialSize);
    }

    public byte[] getInternalBuffer() {
        Objects.requireNonNull(this.myBuffer);
        return this.myBuffer;
    }

    public int backOff(int size) {
        Assert.isTrue(size >= 0);
        this.myCount -= size;
        assert this.myCount >= 0 : this.myCount;
        return this.myCount;
    }
}