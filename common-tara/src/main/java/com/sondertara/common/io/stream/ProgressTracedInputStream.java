package com.sondertara.common.io.stream;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.function.Consumer4;
import com.sondertara.common.progress.ProgressSource;

import java.io.InputStream;

/**
 *  */
public class ProgressTracedInputStream extends WrappedInputStream {
    public ProgressTracedInputStream(InputStream in, final ProgressSource progressSource) {
        super(in, Lists.asList(new Consumer4<InputStream, byte[],Integer, Integer>() {
            Boolean lengthGot = null;

            @Override
            public void accept(InputStream in, byte[] bytes, Integer off, Integer len) {
                if (!progressSource.started()) {
                    progressSource.start();
                }
                progressSource.forward(len);
                if (progressSource.getExpected() < 0 && lengthGot == null) {
                    lengthGot = true;
                    try {
                        long expected = progressSource.getProgress() + in.available();
                        progressSource.update(-1L, expected);
                    } catch (Exception ex) {
                        // ignore
                    }
                }
            }
        }));
    }
}
