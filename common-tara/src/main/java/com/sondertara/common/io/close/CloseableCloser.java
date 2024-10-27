package com.sondertara.common.io.close;


import com.sondertara.common.collection.Lists;

import java.io.Closeable;
import java.util.List;

public class CloseableCloser extends AbstractCloser<Closeable> {
    @Override
    protected void doClose(Closeable closeable) throws Exception{
        closeable.close();
    }

    @Override
    public List<Class> applyTo() {
        return Lists.newArrayList(Closeable.class);
    }
}

