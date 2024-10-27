package com.sondertara.common.collection.diff;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MapDiffResult<K, V> implements CollectionDifferResult<Map<K, V>> {
    private Map<K, V> adds = new HashMap<K, V>();
    private Map<K, V> removes = new HashMap<K, V>();
    private Map<K, V> updates = new HashMap<K, V>();
    private Map<K, V> equals = new HashMap<K, V>();

    @Override
    public Map<K, V> getAdds() {
        return adds;
    }

    public void setAdds(@Nullable Map<K, V> adds) {
        if (ObjectUtils.isNotNull(adds)) {
            this.adds = adds;
        }
    }

    @Override
    public Map<K, V> getRemoves() {
        return removes;
    }

    public void setRemoves(@Nullable Map<K, V> removes) {
        if (ObjectUtils.isNotNull(removes)) {
            this.removes = removes;
        }
    }

    @Override
    public Map<K, V> getUpdates() {
        return updates;
    }

    public void setUpdates(@Nullable Map<K, V> updates) {
        if (ObjectUtils.isNotNull(updates)) {
            this.updates = updates;
        }
    }

    @Override
    public Map<K, V> getEquals() {
        return equals;
    }

    public void setEquals(@Nullable Map<K, V> equals) {
        if (ObjectUtils.isNotNull(equals)) {
            this.equals = equals;
        }
    }

    @Override
    public boolean hasDifference() {
        return Emptys.isNotEmpty(adds) || Emptys.isNotEmpty(updates) || Emptys.isNotEmpty(removes);
    }
}
