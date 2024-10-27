package com.sondertara.common.lifecycle;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.io.close.AbstractCloser;

import java.util.List;

public class LifecycleCloser extends AbstractCloser<Lifecycle> {
    @Override
    public List<Class> applyTo() {
        return Lists.newArrayList(Lifecycle.class);
    }

    @Override
    protected void doClose(Lifecycle lifecycle) throws Exception {
        lifecycle.shutdown();
    }
}
