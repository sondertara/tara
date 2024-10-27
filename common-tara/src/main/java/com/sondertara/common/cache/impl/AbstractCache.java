package com.sondertara.common.cache.impl;

import com.sondertara.common.base.Mutable;
import com.sondertara.common.base.MutableObj;
import com.sondertara.common.cache.Cache;
import com.sondertara.common.cache.CacheListener;
import com.sondertara.common.cache.CacheLoader;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.concurrent.SafeConcurrentHashMap;
import lombok.Getter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 超时和限制大小的缓存的默认实现<br>
 * 继承此抽象缓存需要：<br>
 * <ul>
 * <li>创建一个新的Map</li>
 * <li>实现 {@code prune} 策略</li>
 * </ul>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Looly, jodd
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
    private static final long serialVersionUID = 1L;

    private static final byte UPDATE_AFTER_WRITE= 0x01;
    private static final byte UPDATE_NONE= 0;

    // unit: seconds, 大于 0 时有效，用于在发生了写操作时，更新过期时间。 < 0 时代表无过期时间，即永不过期

    private long expireAfterWrite = -1;
    // unit: seconds, 大于 0 时有效，用于在发生了写操作时，更新过期时间。<=0 时，代表不启用该特性

    private long expireAfterRead = -1;

    private long expireAfterAccess = -1;


    @Getter
    private boolean updateLastAccessTime = false;
    /**
     * 是否清理过期缓存
     */
    private boolean pruneExpired = false;


    private CacheLoader<K, V> globalCacheLoader;

    public void setGlobalCacheLoader(CacheLoader<K, V> globalCacheLoader) {
        this.globalCacheLoader = globalCacheLoader;
    }

    public AbstractCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public V get(K key) {
        if (null == globalCacheLoader) {
            return get(key, updateLastAccessTime);
        } else {
            return get(key, updateLastAccessTime, () -> globalCacheLoader.load(key));
        }
    }


    protected Map<Mutable<K>, CacheObj<K, V>> cacheMap;

    /**
     * 写的时候每个key一把锁，降低锁的粒度
     */
    protected final SafeConcurrentHashMap<K, Lock> keyLockMap = new SafeConcurrentHashMap<>();

    /**
     * 返回缓存容量，{@code 0}表示无大小限制
     */
    protected int capacity;
    /**
     * 缓存写入后失效时长， {@code 0} 表示无限制，单位毫秒
     */
    protected long timeoutAfterWrite;
    /**
     * 缓存读取后失效时长， {@code 0} 表示无限制，单位毫秒
     */
    protected long timeoutAfterRead;

    /**
     * 每个对象是否有单独的失效时长，用于决定清理过期对象是否有必要。
     */
    protected boolean existCustomTimeout;


    /**
     * 命中数，即命中缓存计数
     */
    protected LongAdder hitCount = new LongAdder();
    /**
     * 丢失数，即未命中缓存计数
     */
    protected LongAdder missCount = new LongAdder();

    /**
     * 缓存监听
     */
    protected CacheListener<K, V> listener;

    // ---------------------------------------------------------------- put start
    @Override
    public void put(K key, V object) {
        put(key, object, this.timeoutAfterWrite);
    }

    /**
     * 加入元素，无锁
     *
     * @param key     键
     * @param object  值
     * @param timeout 超时时长
     * @since 4.5.16
     */
    protected void putWithoutLock(K key, V object, long timeout) {
        CacheObj<K, V> co = new CacheObj<>(key, object, timeout);
        if (timeout > 0) {
            existCustomTimeout = true;
        }

        final MutableObj<K> mKey = new MutableObj<>(key);

        // issue#3618 对于替换的键值对，不做满队列检查和清除
        if (cacheMap.containsKey(mKey)) {
            // 存在相同key，覆盖之
            cacheMap.put(mKey, co);
        } else {
            if (isFull()) {
                pruneCache();
            }
            cacheMap.put(mKey, co);
        }
    }
    // ---------------------------------------------------------------- put end

    // ---------------------------------------------------------------- get start

    /**
     * @return 命中数
     */
    public long getHitCount() {
        return hitCount.sum();
    }

    /**
     * @return 丢失数
     */
    public long getMissCount() {
        return missCount.sum();
    }

    @Override
    public V get(K key, boolean isUpdateLastAccess, Supplier<V> supplier) {
        return get(key, isUpdateLastAccess, this.timeoutAfterRead, supplier);
    }

    @Override
    public V get(K key, boolean isUpdateLastAccess, long timeout, Supplier<V> supplier) {
        V v = get(key, isUpdateLastAccess);
        if (null == v && null != supplier) {
            //每个key单独获取一把锁，降低锁的粒度提高并发能力，see pr#1385@Github
            final Lock keyLock = keyLockMap.computeIfAbsent(key, k -> new ReentrantLock());
            keyLock.lock();
            try {
                // 双重检查锁，防止在竞争锁的过程中已经有其它线程写入
                // issue#3686 由于这个方法内的加锁是get独立锁，不和put锁互斥，而put和pruneCache会修改cacheMap，导致在pruneCache过程中get会有并发问题
                // 因此此处需要使用带全局锁的get获取值
                v = get(key, isUpdateLastAccess);
                if (null == v) {
                    // supplier的创建是一个耗时过程，此处创建与全局锁无关，而与key锁相关，这样就保证每个key只创建一个value，且互斥
                    v = supplier.get();
                    put(key, v, timeout);
                }
            } finally {
                keyLock.unlock();
                keyLockMap.remove(key);
            }
        }
        return v;
    }

    /**
     * 获取键对应的{@link CacheObj}
     *
     * @param key 键，实际使用时会被包装为{@link MutableObj}
     * @return {@link CacheObj}
     * @since 5.8.0
     */
    protected CacheObj<K, V> getWithoutLock(K key) {
        return this.cacheMap.get(new MutableObj<>(key));
    }
    // ---------------------------------------------------------------- get end

    @Override
    public Map<K, V> toMap() {
        CacheObjIterator<K, V> copiedIterator = (CacheObjIterator<K, V>) this.cacheObjIterator();
        return StreamUtils.of(copiedIterator).collect(Collectors.toMap(CacheObj::getKey, CacheObj::getValue, (v1, v2) -> v1, LinkedHashMap::new));

    }


    // ---------------------------------------------------------------- prune start

    /**
     * 清理实现<br>
     * 子类实现此方法时无需加锁
     *
     * @return 清理数
     */
    protected abstract int pruneCache();
    // ---------------------------------------------------------------- prune end

    // ---------------------------------------------------------------- common start
    @Override
    public int capacity() {
        return capacity;
    }

    /**
     * 只有设置公共缓存失效时长或每个对象单独的失效时长时清理可用
     *
     * @return 过期对象清理是否可用，内部使用
     */
    protected boolean isPruneExpiredActive() {
        return pruneExpired || existCustomTimeout;
    }

    @Override
    public boolean isFull() {
        return (capacity > 0) && (cacheMap.size() >= capacity);
    }

    @Override
    public int size() {
        return toMap().size();
    }

    @Override
    public boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    @Override
    public String toString() {
        return this.cacheMap.toString();
    }
    // ---------------------------------------------------------------- common end

    /**
     * 设置监听
     *
     * @param listener 监听
     * @return this
     * @since 5.5.2
     */
    @Override
    public AbstractCache<K, V> setListener(CacheListener<K, V> listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 返回所有键
     *
     * @return 所有键
     * @since 5.5.9
     */
    public Set<K> keySet() {
        return this.cacheMap.keySet().stream().map(Mutable::get).collect(Collectors.toSet());
    }

    /**
     * 对象移除回调。默认无动作<br>
     * 子类可重写此方法用于监听移除事件，如果重写，listener将无效
     *
     * @param key          键
     * @param cachedObject 被缓存的对象
     */
    protected void onRemove(K key, V cachedObject) {
        final CacheListener<K, V> listener = this.listener;
        if (null != listener) {
            listener.onRemove(key, cachedObject);
        }
    }

    /**
     * 移除key对应的对象，不加锁
     *
     * @param key 键
     * @return 移除的对象，无返回null
     */
    protected CacheObj<K, V> removeWithoutLock(K key) {
        return cacheMap.remove(new MutableObj<>(key));
    }

    /**
     * 获取所有{@link CacheObj}值的{@link Iterator}形式
     *
     * @return {@link Iterator}
     * @since 5.8.0
     */
    protected Iterator<CacheObj<K, V>> cacheObjIter() {
        return this.cacheMap.values().iterator();
    }


    public void setExpireAfterRead(long expireAfterRead) {
        this.expireAfterRead = expireAfterRead;
        this.init();
    }

    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        this.init();
    }

    public void setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        this.init();
    }


    private void init() {
        if (expireAfterAccess > 0 || expireAfterRead > 0) {
            this.updateLastAccessTime = true;
            this.pruneExpired=true;
            this.timeoutAfterRead = TimeUnit.SECONDS.toMillis(Math.max(expireAfterRead, expireAfterAccess));
        }
        if (this.expireAfterWrite > 0 || this.expireAfterAccess > 0) {
            this.pruneExpired=true;
            this.timeoutAfterWrite = TimeUnit.SECONDS.toMillis(Math.max(expireAfterWrite, expireAfterAccess));
        }
    }
}
