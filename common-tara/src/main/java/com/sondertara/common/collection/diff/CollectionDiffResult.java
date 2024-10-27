package com.sondertara.common.collection.diff;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @param <E>
 * @author jinuo.fang
 */
public class CollectionDiffResult<E> implements CollectionDifferResult<Collection<E>> {

    private Collection<E> adds = new ArrayList<E>();
    private Collection<E> removes = new ArrayList<E>();
    private Collection<E> updates = new ArrayList<E>();
    private Collection<E> equals = new ArrayList<E>();

    @Override
    public Collection<E> getAdds() {
        return adds;
    }

    public void setAdds(@Nullable Collection<E> adds) {
        if (ObjectUtils.isNotNull(adds)) {
            this.adds = adds;
        }
    }

    @Override
    public Collection<E> getRemoves() {
        return removes;
    }

    public void setRemoves(@Nullable Collection<E> removes) {
        if (ObjectUtils.isNotNull(removes)) {
            this.removes = removes;
        }
    }

    @Override
    public Collection<E> getUpdates() {
        return updates;
    }

    public void setUpdates(@Nullable Collection<E> updates) {
        if (ObjectUtils.isNotNull(updates)) {
            this.updates = updates;
        }
    }

    @Override
    public Collection<E> getEquals() {
        return equals;
    }

    public void setEquals(@Nullable Collection<E> equals) {
        if (ObjectUtils.isNotNull(equals)) {
            this.equals = equals;
        }
    }

    @Override
    public boolean hasDifference() {
        return Emptys.isNotEmpty(adds) || Emptys.isNotEmpty(updates) || Emptys.isNotEmpty(removes);
    }
}
