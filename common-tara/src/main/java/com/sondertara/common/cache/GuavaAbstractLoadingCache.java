package com.sondertara.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.sondertara.common.exception.TaraException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象Guava缓存类、缓存模板。
 * 子类需要实现fetchData(key)，从数据库或其他数据源（如Redis）中获取数据。
 * 子类调用getValue(key)方法，从缓存中获取数据，并处理不同的异常，比如value为null时的InvalidCacheLoadException异常。
 *
 * @param <K> key 类型
 * @param <V> value 类型
 * @author huangxiaohu
 */
@Slf4j
public abstract class GuavaAbstractLoadingCache<K, V> implements ILocalCache<K, V> {

    /**
     * 最大缓存条数，子类在构造方法中调用setMaximumSize(int size)来更改
     */
    private int maximumSize = 1000;
    /**
     * 数据存在时长，子类在构造方法中调用setExpireAfterWriteDuration(int duration)来更改
     */
    private int expireAfterWriteDuration = 60;
    /**
     *
     */
    private final TimeUnit timeUnit = TimeUnit.SECONDS;
    /**
     * Cache初始化或被重置的时间
     */
    private Date resetTime;
    /**
     * 历史最高记录数
     */
    private volatile long highestSize = 0;
    /**
     * 创造历史记录的时间
     */
    private Date highestTime;

    private volatile LoadingCache<K, Optional<V>> cache;
    private final AtomicBoolean reload = new AtomicBoolean(false);

    /**
     * 通过调用getCache().get(key)来获取数据
     *
     * @return cache
     */
    private LoadingCache<K, Optional<V>> getCache() {

        if (reload.get()) {
            cache = null;
        }
        // 使用双重校验锁保证只有一个cache实例
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = CacheBuilder.newBuilder().maximumSize(maximumSize)
                            .expireAfterWrite(expireAfterWriteDuration, timeUnit).recordStats()
                            .build(new CacheLoader<K, Optional<V>>() {

                                @Override
                                public Optional<V> load(K key) {
                                    return fetchData(key);
                                }
                            });
                    this.resetTime = new Date();
                    this.highestTime = new Date();
                    this.reload.set(false);
                    log.info("本地缓存{}初始化成功,过期时间{}s", this, this.expireAfterWriteDuration);
                }
            }
        }
        return cache;
    }

    public void put(K key, V value) {
        getCache().put(key, Optional.ofNullable(value));
    }

    /**
     * 根据key从数据库或其他数据源中获取一个value，并被自动保存到缓存中。
     *
     * @param key the key not be null
     * @return value, 连同key一起被加载到缓存中的。
     */
    protected abstract Optional<V> fetchData(K key);

    /**
     * 从缓存中获取数据（第一次自动调用fetchData从外部获取数据），并处理异常
     *
     * @param key
     * @return Value
     * @throws TaraException 业务异常
     */
    protected Optional<V> getValue(K key) throws TaraException {
        try {
            if (null == key) {
                return Optional.empty();
            }
            Optional<V> optional = getCache().get(key);
            if (getCache().size() > highestSize) {
                highestSize = getCache().size();
                highestTime = new Date();
            }
            if (log.isDebugEnabled()) {
                log.debug("Get cache success,key=[{}]", key);
            }
            return optional;
        } catch (Exception e) {
            if (e instanceof UncheckedExecutionException && e.getCause() != null
                    && e.getCause() instanceof TaraException) {
                log.error("key={} get cache failed", key, e.getCause());
                throw (TaraException) e.getCause();
            } else {
                log.error("key={} get cache failed,{}", key, e.getMessage());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<V> get(K key) {
        return getValue(key);
    }

    public void invalidateAll() {
        getCache().invalidateAll();
    }

    public void invalidate(K key) {
        getCache().invalidate(key);
    }

    public long getHighestSize() {
        return highestSize;
    }

    public Date getHighestTime() {
        return highestTime;
    }

    public Date getResetTime() {
        return resetTime;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    public int getExpireAfterWriteDuration() {
        return expireAfterWriteDuration;
    }

    /**
     * 设置最大缓存条数
     *
     * @param maximumSize
     */
    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    /**
     * 设置数据存在时长（秒）
     *
     * @param expireAfterWriteDuration
     */
    public void setExpireAfterWriteDuration(int expireAfterWriteDuration) {
        this.expireAfterWriteDuration = expireAfterWriteDuration;
        reload();
    }

    public void reload() {
        getCache().cleanUp();
        this.reload.set(true);
    }
}