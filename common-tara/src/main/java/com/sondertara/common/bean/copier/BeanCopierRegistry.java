package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.copier.BeanCopier;
import com.sondertara.common.lang.Pair;
import com.sondertara.common.lang.map.WeakConcurrentMap;
import com.sondertara.common.lang.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Registry of all generated bean copiers.
 *
 * @author huangxiaohu
 */
public class BeanCopierRegistry {
    private static final Map<Pair<Class<?>, Class<?>>, BeanCopier> TOP_BEAN_COPIER_MAP = new WeakConcurrentMap<>();
    private static final Map<Pair<Field, Field>, BeanCopier> REF_BEAN_COPIER_MAP = new WeakConcurrentMap<>();
    private static final Map<Class<?>, Map<String, Field>> SIMPLE_CLASS_COPIER_MAP = new WeakConcurrentMap<>();

    /**
     * Prepare a bean copier before using, in order to check correctness and warm-up
     * cache in advance
     */
    public static BeanCopier prepare(Class<?> sourceCls, Class<?> targetCls) {
        Pair<Class<?>, Class<?>> pair = Pair.of(sourceCls, targetCls);
        BeanCopier beanCopier = TOP_BEAN_COPIER_MAP.get(pair);
        if (beanCopier == null) {
            beanCopier = new BeanCopier(sourceCls, targetCls);
            TOP_BEAN_COPIER_MAP.put(pair, beanCopier);
            beanCopier.ensureAnalyzed();
        }
        return beanCopier;
    }

    static BeanCopier findOrCreate(Field fromField, Field toField) {
        Pair<Field, Field> pair = Pair.of(fromField, toField);
        BeanCopier beanCopier = REF_BEAN_COPIER_MAP.get(pair);
        if (beanCopier == null) {
            beanCopier = new BeanCopier(fromField, toField);
            REF_BEAN_COPIER_MAP.put(pair, beanCopier);
        }
        return beanCopier;
    }

    static void clear() {
        TOP_BEAN_COPIER_MAP.clear();
        REF_BEAN_COPIER_MAP.clear();
    }

    static Map<String, Field> findOrCreate(Class<?> targetCls) {
        return SIMPLE_CLASS_COPIER_MAP.computeIfAbsent(targetCls, key -> ReflectUtils.getFieldMap(targetCls));
    }
}
