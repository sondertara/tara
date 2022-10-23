package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.exception.BeanAnalysisException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Copy properties from an instance of AClass to an instance of BClass,
 * even if AClass and BClass have nested structures and collections/maps of
 * instances of other classes.
 * <p>
 * You can also do batch copying on collections or maps.
 * <p>
 * Bean copiers are cached, so that it's faster at next time.
 *
 * @author huangxiaohu
 * @see BeanCopierRegistry You can prepare bean copiers before using, in order
 * to early check correctness and warm-up cache.
 * @see ConverterRegistry Before the first call of BeanCopy, you can add custom
 * converters to supply or override the default behavior.
 */
@SuppressWarnings("unchecked")
public class BeanCopy {

    /**
     * Copy properties of source to a new instance of targetCls
     */
    public static <R> R copy(Object source, Class<R> targetCls) {
        return (R) BeanCopierRegistry.prepare(source.getClass(), targetCls).topCopyWithoutTopConverter(source);
    }

    public static <R> R copyIgnoreNull(Object source, Class<R> targetCls) {
        BeanCopier beanCopier = BeanCopierRegistry.prepare(source.getClass(), targetCls);
        beanCopier.setIgnoreNull(true);
        return (R) beanCopier.topCopyWithoutTopConverter(source);
    }

    /**
     * <b>Caution:</b> Ignores the converter of source->target (if any)
     */
    public static void copyTo(Object source, Object target) {
        BeanCopierRegistry.prepare(source.getClass(), target.getClass()).topCopyWithoutTopConverter(source, target);
    }

    public static void copyToIgnoreNull(Object source, Object target) {
        BeanCopier copier = BeanCopierRegistry.prepare(source.getClass(), target.getClass());
        copier.setIgnoreNull(true);
        copier.topCopyWithoutTopConverter(source, target);
    }

    /**
     * Copy each element of sources and add to targets
     */
    public static void copy(Collection<?> sources, Collection<?> targets, Class<?> targetCls) {
        Collection<Object> results = (Collection<Object>) targets;
        Object sourceObj = sources.stream().filter(Objects::nonNull).findFirst().orElseThrow(() -> new BeanAnalysisException("The source collection is empty"));
        BeanCopier elemCopier = BeanCopierRegistry.prepare(sourceObj.getClass(), targetCls);
        for (Object source : sources) {
            results.add(elemCopier.topCopyWithoutTopConverter(source));
        }
    }

    /**
     * Copy each element of sources and add to targets
     */
    public static <K> void copy(Map<K, ?> sources, Map<K, ?> targets, Class<?> sourceCls, Class<?> targetCls) {
        Map<K, Object> results = (Map<K, Object>) targets;
        BeanCopier elemCopier = BeanCopierRegistry.prepare(sourceCls, targetCls);
        for (Map.Entry<K, ?> source : sources.entrySet()) {
            results.put(source.getKey(), elemCopier.topCopyWithoutTopConverter(source.getValue()));
        }
    }
}
