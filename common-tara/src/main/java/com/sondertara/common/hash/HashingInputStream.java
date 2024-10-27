package com.sondertara.common.hash;


import com.sondertara.common.io.stream.IOStreamPipeline;
import com.sondertara.common.io.stream.InputStreamInterceptor;
import com.sondertara.common.io.stream.WrappedInputStream;

import java.io.InputStream;

/**
 *  */
public final class HashingInputStream extends WrappedInputStream {

    /**
     * Creates an input stream that hashes using the given {@link StreamingHasher} and delegates all data
     * read from it to the underlying {@link InputStream}.
     *
     * <p>The {@link InputStream} should not be read from before or after the hand-off.
     */
    public HashingInputStream(final StreamingHasher hasher, InputStream in) {
        super(in, IOStreamPipeline.of(new InputStreamInterceptor() {
            @Override
            public boolean beforeRead(InputStream inputStream, byte[] b, int off, int len) {
                return true;
            }

            @Override
            public boolean afterRead(InputStream inputStream, byte[] b, int off, int len) {
                hasher.update(b, off, len);
                return true;
            }
        }));
    }

}
