package com.sondertara.common.collection.diff;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.equator.DiffFieldInfo;
import com.sondertara.common.equator.DifferType;
import com.sondertara.common.equator.Equator;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @param <K>
 * @param <V>
 * @author jinuo.fang
 */
public class MapDiffer<K, V>  implements Equator,Differ<Map<K, V>, MapDiffResult<K, V>> {
    @Nullable
    private Comparator<K> keyComparator;
    @Nullable
    private Comparator<V> valueComparator;

    @Override
    @SuppressWarnings("unchecked")
    public List<DiffFieldInfo> getDiffFields(Object first, Object second) {
        Map<K, V> firstMap = (Map<K, V>) first;

        MapDiffResult<K, V> result = diff(firstMap, (Map<K, V>) second);
        List<DiffFieldInfo> diffFieldInfoList =new ArrayList<>();

        if (result.hasDifference()){
            result.getAdds().forEach((k, v) -> diffFieldInfoList.add(new DiffFieldInfo(DifferType.ADDED,k.toString(), v.getClass(),null, v)));
            result.getUpdates().forEach((k, v) -> diffFieldInfoList.add(new DiffFieldInfo(DifferType.UPDATED,k.toString(),v.getClass(), firstMap.get(k), v)));
            result.getRemoves().forEach((k, v) -> diffFieldInfoList.add(new DiffFieldInfo(DifferType.REMOVED,k.toString(), v.getClass(),v, null)));
        }

        return diffFieldInfoList;
    }

    @Override
    public boolean isEquals(Object first, Object second) {
        return CollectionUtils.isEmpty(getDiffFields(first, second));
    }

    private List<DifferType> differTypes = Lists.asList(EnumSet.allOf(DifferType.class));

    public List<DifferType> getJudgeTypes() {
        return differTypes;
    }

    public void setJudgeTypes(List<DifferType> differTypes) {
        if (differTypes != null) {
            this.differTypes = differTypes;
        }
    }


    public void setKeyComparator(@Nullable Comparator<K> comparator) {
        this.keyComparator = comparator;
    }

    public void setValueComparator(@Nullable Comparator<V> comparator) {
        this.valueComparator = comparator;
    }

    @Override
    public MapDiffResult<K, V> diff(@Nullable final Map<K, V> oldMap, @Nullable final Map<K, V> newMap) {
        MapDiffResult<K, V> result = new MapDiffResult<K, V>();

        if (oldMap == null && newMap == null) {
            return result;
        }

        if (newMap == null) {
            result.setRemoves(oldMap);
            return result;
        }

        if (oldMap == null) {
            result.setAdds(newMap);
            return result;
        }

        Set<K> oldKeys = oldMap.keySet();
        Set<K> newKeys = newMap.keySet();
        CollectionDiffer<K> keyDiffer = new CollectionDiffer<K>();
        keyDiffer.setComparator(keyComparator);
        keyDiffer.setJudgeTypes(getJudgeTypes());
        CollectionDiffResult<K> keyDiffResult = keyDiffer.diff(oldKeys, newKeys);

        Collection<K> addsKeys = keyDiffResult.getAdds();
        final Map<K, V> addsMap = new HashMap<K, V>();
        CollectionUtils.forEach(addsKeys, new Consumer<K>() {
            @Override
            public void accept(K key) {
                addsMap.put(key, newMap.get(key));
            }
        });

        final Map<K, V> removesMap = new HashMap<K, V>();
        Collection<K> removesKeys = keyDiffResult.getRemoves();
        CollectionUtils.forEach(removesKeys, new Consumer<K>() {
            @Override
            public void accept(K key) {
                removesMap.put(key, oldMap.get(key));
            }
        });

        Collection<K> keyEquals = keyDiffResult.getEquals();
        final Map<K, V> equalsMap = new HashMap<K, V>();
        final Map<K, V> updatesMap = new HashMap<K, V>();
        if (differTypes.contains(DifferType.EQUALED) || differTypes.contains(DifferType.UPDATED)) {
            CollectionUtils.forEach(keyEquals, new Consumer<K>() {
                @Override
                public void accept(K key) {
                    V oldValue = oldMap.get(key);
                    V newValue = newMap.get(key);
                    if (valueComparator == null) {
                        if (oldValue.equals(newValue)) {
                            equalsMap.put(key, newValue);
                        } else {
                            updatesMap.put(key, newValue);
                        }
                    } else {
                        if (valueComparator.compare(oldValue, newValue) == 0) {
                            equalsMap.put(key, newValue);
                        } else {
                            updatesMap.put(key, newValue);
                        }
                    }
                }
            });
        }

        result.setAdds(addsMap);
        result.setRemoves(removesMap);
        result.setEquals(equalsMap);
        result.setUpdates(updatesMap);
        return result;
    }

}
