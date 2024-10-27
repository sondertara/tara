package com.sondertara.common.lifecycle;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.io.close.AbstractCloser;

import java.util.List;

public class DestroyableCloser extends AbstractCloser<Destroyable> {
    @Override
    public List<Class> applyTo() {
        return Lists.newArrayList(Destroyable.class);
    }

    @Override
    protected void doClose(Destroyable destroyable) throws Exception {
        destroyable.destroy();
    }
}
