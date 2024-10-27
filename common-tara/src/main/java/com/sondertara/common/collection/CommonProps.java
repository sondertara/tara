package com.sondertara.common.collection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CommonProps<P> implements Serializable {
    private static final long serialVersionUID = 1L;

    protected transient Map<String, P> props;

    public Map<String, P> getProps() {
        return props;
    }

    public void setProps(Map<String, P> props) {
        this.props = props;
    }

    public MapAccessor getPropsAccessor() {
        return props == null ? new MapAccessor(new HashMap<>(16)) : new MapAccessor(props);
    }

}
