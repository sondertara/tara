package com.sondertara.common.collection.diff;

/**
 * @param <C> the {@link java.util.Collection} or {@link java.util.Map}
 * @author jinuo.fang
 * @see CollectionDiffResult
 * @see MapDiffResult
 */
public interface CollectionDifferResult<C> extends DiffResult {
    C getAdds();

    C getRemoves();

    C getUpdates();

    C getEquals();
}
