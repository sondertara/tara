package com.sondertara.common.cache;


import com.sondertara.common.base.Assert;
import com.sondertara.common.cache.impl.AbstractCache;
import com.sondertara.common.cache.impl.LRUCache;
import com.sondertara.common.os.JdkUtils;
import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.common.reflect.reference.ReferenceType;
import com.sondertara.common.text.StringTemplates;
import com.sondertara.common.timing.timer.Timer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author huangxiaohu.1ih
 */
@SuppressWarnings({"unused", "rawtypes"})
public class CacheBuilder<K, V> {
    private Class cacheClass = LRUCache.class;
    private int concurrencyLevel = JdkUtils.cpuCore();

    // unit: seconds
    private long expireAfterWrite = -1;
    private long expireAfterAccess = -1;
    // unit: seconds
    private long expireAfterRead = -1;
    // unit: seconds
    private long refreshAfterAccess = -1;

    // unit: mills
    private long refreshAllInterval = TimeUnit.HOURS.toMillis(1);

    // unit: mills
    private long evictExpiredInterval = 5 * 60 * 1000L;
    private int maxCapacity = Integer.MAX_VALUE;

    private Timer timer;

    private float capacityHeightWater = 0.95f;
    private ReferenceType keyReferenceType = ReferenceType.STRONG;
    private ReferenceType valueReferenceType = ReferenceType.STRONG;
    private CacheListener<K, V> cacheListener;

    private CacheBuilder() {

    }

    public static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<K, V>();
    }

    @SuppressWarnings("rawtypes")
    public CacheBuilder<K, V> cacheClass(Class cacheClass) {
        this.cacheClass = cacheClass;
        return this;
    }

    public CacheBuilder<K, V> concurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public CacheBuilder<K, V> expireAfterWrite(long expireAfterWriteInSeconds) {
        this.expireAfterWrite = expireAfterWriteInSeconds;
        return this;
    }

    public CacheBuilder<K, V> expireAfterRead(long expireAfterReadInSeconds) {
        this.expireAfterRead = expireAfterReadInSeconds;
        return this;
    }

    public CacheBuilder<K, V> expireAfterAccess(long expireAfterAccessInSeconds) {
        this.expireAfterAccess = expireAfterAccessInSeconds;
        return this;
    }

    /**
     * Set the period of global evict, default : Long.MAX_VALUE
     * unit: mills
     *
     * @param evictExpiredIntervalInMills the evict period
     * @return the cache builder
     * @deprecated
     */
    @Deprecated
    public CacheBuilder<K, V> evictExpiredInterval(long evictExpiredIntervalInMills, Timer timer) {
        return evictExpiredInterval(evictExpiredIntervalInMills).timer(timer);
    }

    public CacheBuilder<K, V> evictExpiredInterval(long evictExpiredIntervalInMills) {
        this.evictExpiredInterval = evictExpiredIntervalInMills;
        return this;
    }

    public CacheBuilder<K, V> timer(Timer timer) {
        this.timer = timer;
        return this;
    }

    public CacheBuilder<K, V> refreshAllInterval(long refreshIntervalInMills) {
        this.refreshAllInterval = refreshIntervalInMills;
        return this;
    }


    public CacheBuilder<K, V> cacheListener(CacheListener<K, V> cacheListener) {
        this.cacheListener = cacheListener;
        return this;
    }

    public CacheBuilder<K, V> maxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public CacheBuilder<K, V> weakValue(boolean weakValue) {
        if (weakValue) {
            this.valueReferenceType = ReferenceType.WEAK;
        }
        return this;
    }

    public CacheBuilder<K, V> softValue(boolean softValue) {
        if (softValue) {
            this.valueReferenceType = ReferenceType.SOFT;
        }
        return this;
    }

    public CacheBuilder<K, V> weakKey(boolean weakKey) {
        if (weakKey) {
            this.keyReferenceType = ReferenceType.WEAK;
        }
        return this;
    }

    public CacheBuilder<K, V> softKey(boolean softKey) {
        if (softKey) {
            this.keyReferenceType = ReferenceType.SOFT;
        }
        return this;
    }


    public <K1 extends K, V1 extends V> Cache<K1, V1>  build() {
        return build(null);
    }

    @SuppressWarnings("unchecked")
    public <K1 extends K, V1 extends V> Cache<K1, V1> build(CacheLoader<K1, V1> cacheLoader) {
        Objects.requireNonNull(cacheClass, "Please specify your cache class");
        Assert.isTrue(AbstractCache.class.isAssignableFrom(cacheClass), StringTemplates.formatWithPlaceholder("Your cache class {} is not a subclass of {}", ReflectUtils.getFQNClassName(cacheClass), ReflectUtils.getFQNClassName(AbstractCache.class)));
        Integer capacity = maxCapacity < 0 ? Integer.MAX_VALUE : maxCapacity;
        AbstractCache<K, V> cache = (AbstractCache<K, V>) ReflectUtils.newInstance(cacheClass, capacity);
        Objects.requireNonNull(cache);
        cache.setExpireAfterRead(expireAfterRead);
        cache.setExpireAfterWrite(expireAfterWrite);
        cache.setExpireAfterAccess(expireAfterAccess);
        cache.setGlobalCacheLoader((CacheLoader<K, V>) cacheLoader);
        cache.setListener(cacheListener);
        return (Cache<K1, V1>) cache;
    }
}
