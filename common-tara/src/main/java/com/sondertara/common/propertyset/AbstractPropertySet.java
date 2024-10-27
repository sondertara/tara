package com.sondertara.common.propertyset;

import com.sondertara.common.base.AbstractNameable;
import com.sondertara.common.base.Emptys;

import java.util.Objects;

/**
 *  */
public abstract class AbstractPropertySet<SRC> extends AbstractNameable implements PropertySet<SRC> {
    private SRC source;
    public AbstractPropertySet(String name){
        this(name, (SRC) Emptys.EMPTY_OBJECTS);
    }
    public AbstractPropertySet(String name, SRC source){
        this.setName(name);
        this.source=source;
    }

    @Override
    public SRC getSource() {
        return source;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==this){
            return true;
        }
        if(!(obj instanceof PropertySet)){
            return false;
        }
        PropertySet that=(PropertySet)obj;
        if(!Objects.equals(this.getName(), that.getName())){
            return false;
        }

        return true;
    }

    public int hashCode(){
        return Objects.hash(this.name);
    }

    @Override
    public boolean containsProperty(String key){
        return getProperty(key) != null;
    }
}
