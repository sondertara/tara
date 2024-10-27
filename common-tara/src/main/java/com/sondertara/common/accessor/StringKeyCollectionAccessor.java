package com.sondertara.common.accessor;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.math.Numbers;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  */
public class StringKeyCollectionAccessor extends BasedStringAccessor<String, Collection<?>> {

    @Override
    public void setTarget(@NonNull Collection<?> target) {
        super.setTarget(new ArrayList<>(target));
    }

    public void setTarget(Object ... elements){
        this.setTarget(Lists.asList(elements));
    }

    @Override
    public void remove(String key) {
        getTarget().remove(toIndex(key));
    }

    private int toIndex(String key){
        return Numbers.createInteger(key);
    }

    @Override
    public Object get(String key) {
        return ((List)getTarget()).get(toIndex(key));
    }

    @Override
    public String getString(String key, String defaultValue) {
        Object element = ((List)getTarget()).get(toIndex(key));
        if(ObjectUtils.isNotNull(element)){
            return element.toString();
        }else{
            return defaultValue;
        }
    }

    @Override
    public void set(String key, Object value) {
        ((List)getTarget()).set(toIndex(key),value);
    }
}
