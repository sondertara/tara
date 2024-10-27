package com.sondertara.common.collection;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.accessor.BasedStringAccessor;

import java.util.List;

public class ListAccessor extends BasedStringAccessor<Integer, List> {
    @Override
    public void remove(Integer index) {
        getTarget().remove(index);
    }

    @Override
    public Object get(Integer index) {
        return getTarget().get(index);
    }

    @Override
    public String getString(Integer index, String defaultValue) {
        Object element = getTarget().get(index);
        if(ObjectUtils.isNotNull(element)){
            return element.toString();
        }else{
            return defaultValue;
        }
    }

    @Override
    public void set(Integer index, Object value) {
        getTarget().set(index, value);
    }
}
