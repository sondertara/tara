package com.sondertara.common.io.stream;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.function.Consumer4;
import com.sondertara.common.progress.ProgressSource;

import java.io.OutputStream;

/**
 *  */
public class ProgressTracedOutputStream extends WrappedOutputStream {
    public ProgressTracedOutputStream(OutputStream in, final ProgressSource progressSource) {
        super(in, Lists.asList(new Consumer4<OutputStream, byte[], Integer, Integer>() {
            @Override
            public void accept(OutputStream in, byte[] bytes, Integer off, Integer len) {
                if (!progressSource.started()) {
                    progressSource.start();
                }
                progressSource.forward(len);
            }
        }));
    }
}