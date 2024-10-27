package com.sondertara.common.collection.multivalue;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class MultiValueMapAdapter<K, V> extends CommonMultiValueMap<K, V> implements Serializable {


    public MultiValueMapAdapter(Map<K, Collection<V>> map) {
        super(map, input -> Collections.emptyList());
    }


}