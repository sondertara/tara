package com.sondertara.common.io.close;

import com.sondertara.common.collection.Lists;
import com.sondertara.common.reflect.ReflectUtils;

import java.util.List;

public class ForceCloser extends AbstractCloser<Object> {
    @Override
    public List<Class> applyTo() {
        return Lists.newArrayList(Object.class);
    }

    @Override
    protected void doClose(Object o) throws Exception {
        ReflectUtils.invokeAnyMethodForcedIfPresent(o, "close", null, null);
    }
}
