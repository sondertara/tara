package com.sondertara.common.reflect;

import com.sondertara.common.reflect.reference.ReferenceType;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.function.Supplier;

/**
 * 引用工具类，主要针对{@link Reference} 工具化封装<br>
 * 主要封装包括：
 *
 * <pre>
 * 1. {@link SoftReference} 软引用，在GC报告内存不足时会被GC回收
 * 2. {@link WeakReference} 弱引用，在GC时发现弱引用会回收其对象
 * 3. {@link PhantomReference} 虚引用，在GC时发现虚引用对象，会将{@link PhantomReference}插入{@link ReferenceQueue}。 此时对象未被真正回收，要等到{@link ReferenceQueue}被真正处理后才会被回收。
 * </pre>
 *
 * @author huangxiaohu
 */
public class ReferenceUtils {

    /**
     * 获得引用
     *
     * @param <T>      被引用对象类型
     * @param type     引用类型枚举
     * @param referent 被引用对象
     * @return {@link Reference}
     */
    public static <T> Supplier<T> create(ReferenceType type, T referent) {
        return create(type, referent, null);
    }

    /**
     * 获得引用
     *
     * @param <T>      被引用对象类型
     * @param type     引用类型枚举
     * @param referent 被引用对象
     * @param queue    引用队列
     * @return {@link Reference}
     */
    public static <T> Supplier<T> create(ReferenceType type, T referent, ReferenceQueue<T> queue) {
        switch (type) {
            case SOFT:
                return () -> new SoftReference<>(referent, queue).get();
            case WEAK:
                return () -> new WeakReference<>(referent, queue).get();
            case PHANTOM:
                return () -> new PhantomReference<>(referent, queue).get();
            default:
                return () -> referent;
        }
    }


    /**
     * 引用类型
     *
     * @author huangxiaohu
     */


}
