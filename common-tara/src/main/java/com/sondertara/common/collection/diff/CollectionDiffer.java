package com.sondertara.common.collection.diff;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.struct.Pair;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.comparator.EqualsComparator;
import com.sondertara.common.equator.DifferType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <E>
 * @author jinuo.fang
 */
public class CollectionDiffer<E>  implements  Differ<Collection<E>, CollectionDiffResult<E>> {
    private Comparator<E> comparator;
    private KeyBuilder<String, E> keyBuilder;
    private List<DifferType> differTypes = Lists.asList(EnumSet.allOf(DifferType.class));

    public List<DifferType> getJudgeTypes() {
        return differTypes;
    }

    public void setJudgeTypes(List<DifferType> differTypes) {
        if (differTypes != null) {
            this.differTypes = differTypes;
        }
    }

    public void diffUsingMap(@Nullable KeyBuilder<String, E> keyBuilder) {
        this.keyBuilder = keyBuilder;
    }

    public void setComparator(@Nullable Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public CollectionDiffResult<E> diff(@Nullable Collection<E> oldCollection, @Nullable Collection<E> newCollection) {
        CollectionDiffResult<E> result = new CollectionDiffResult<E>();

        if (ObjectUtils.isEmpty(oldCollection) && ObjectUtils.isEmpty(newCollection)) {
            return result;
        }

        if (oldCollection == null) {
            result.setAdds(newCollection);
            return result;
        }

        if (newCollection == null) {
            result.setRemoves(oldCollection);
            return result;
        }

        if (isDiffUsingMapDiffer()) {
            Map<String, E> oldMap = CollectionUtils.toMap(oldCollection, (Function<E, Pair<String, E>>) element -> new Pair<>(keyBuilder.getKey(element), element));

            Map<String, E> newMap = CollectionUtils.toMap(newCollection, (Function<E, Pair<String, E>>) element -> new Pair<>(keyBuilder.getKey(element), element));

            MapDiffer<String, E> mapDiffer = new MapDiffer<String, E>();
            mapDiffer.setValueComparator(comparator);
            mapDiffer.setKeyComparator(new EqualsComparator<String>());
            mapDiffer.setJudgeTypes(differTypes);
            MapDiffResult<String, E> dr = mapDiffer.diff(oldMap, newMap);
            result.setAdds(dr.getAdds().values());
            result.setRemoves(dr.getRemoves().values());
            result.setEquals(dr.getEquals().values());
            result.setUpdates(dr.getUpdates().values());
        } else {
            doDiff(oldCollection, newCollection, result);
        }
        return result;
    }

    private boolean isDiffUsingMapDiffer() {
        return this.keyBuilder != null;
    }


    private void doDiff(final Collection<E> oldCollection, final Collection<E> newCollection, CollectionDiffResult<E> result) {
        final List<E> adds = new ArrayList<E>();
        final List<E> removes = new ArrayList<E>();
        final List<E> equals = new ArrayList<E>();
        final Comparator<E> comp = comparator == null ? new EqualsComparator<E>() : comparator;
        if (differTypes.contains(DifferType.ADDED) || differTypes.contains(DifferType.EQUALED)) {
            CollectionUtils.forEach(newCollection, new Consumer<E>() {
                @Override
                public void accept(final E newValue) {
                    if (CollectionUtils.anyMatch(oldCollection, new Predicate<E>() {
                        @Override
                        public boolean test(E oldValue) {
                            return comp.compare(oldValue, newValue) == 0;
                        }
                    })) {
                        equals.add(newValue);
                    } else {
                        adds.add(newValue);
                    }
                }
            });
        }
        if (differTypes.contains(DifferType.REMOVED)) {
            CollectionUtils.forEach(oldCollection, new Consumer<E>() {
                @Override
                public void accept(final E oldValue) {
                    if (CollectionUtils.noneMatch(newCollection, new Predicate<E>() {
                        @Override
                        public boolean test(E newValue) {
                            return comp.compare(oldValue, newValue) == 0;
                        }
                    })) {
                        removes.add(oldValue);
                    }
                }
            });
        }

        result.setAdds(adds);
        result.setRemoves(removes);
        result.setEquals(equals);
    }
}
