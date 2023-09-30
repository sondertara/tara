package com.sondertara.common.threadpool;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理类
 *
 * @author walter.tan
 */
@Slf4j
class ThreadPoolManager implements ConfigureContext {

    private static final ConcurrentHashMap<String, ThreadPoolDelegate> THREAD_POOLS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ScheduledThreadPoolDelegate> SCHEDULED_POOLS = new ConcurrentHashMap<>();

    private static final Map<String, ThreadPoolConfigure> POOL_CONFIGURE_MAP = Maps.newConcurrentMap();

    ThreadPoolManager() {
    }

    @Override
    public ThreadPoolExecutor createThreadPool(ThreadPoolConfigure conf) {
        POOL_CONFIGURE_MAP.computeIfAbsent(conf.getKey(), key -> {
            conf.setCreateTime(System.currentTimeMillis());
            return conf;
        });
        return THREAD_POOLS.computeIfAbsent(conf.getKey(), key -> new ThreadPoolDelegate(conf));
    }

    public ScheduledExecutorService createSchedulePool(ThreadPoolConfigure conf) {
        conf.setScheduledPool(true);
        POOL_CONFIGURE_MAP.computeIfAbsent(conf.getKey(), key -> {
            conf.setCreateTime(System.currentTimeMillis());
            return conf;
        });
        return SCHEDULED_POOLS.computeIfAbsent(conf.getKey(), key -> new ScheduledThreadPoolDelegate(conf));
    }


    public static Map<String, ThreadPoolDelegate> allThreadPool() {
        return THREAD_POOLS;
    }


    public void shutdown(String type) {
        ThreadPoolExecutor pool = THREAD_POOLS.get(type);
        if (null != pool) {
            ThreadUtils.shutdownThreadPool(pool);
            THREAD_POOLS.remove(type);
        }
        ScheduledThreadPoolDelegate poolDelegate = SCHEDULED_POOLS.get(type);
        if (null != poolDelegate) {
            ThreadUtils.shutdownThreadPool(poolDelegate);
            SCHEDULED_POOLS.remove(type);
        }

    }

    public void clear(String name) {
        log.info("Clear thread pool tasks start for name[{}]", name);
        ThreadPoolDelegate threadPoolExecutor = THREAD_POOLS.get(name);
        if (threadPoolExecutor != null) {
            threadPoolExecutor.clear();
        }
        ScheduledThreadPoolDelegate scheduledThreadPoolDelegate = SCHEDULED_POOLS.get(name);
        if (null != scheduledThreadPoolDelegate) {
            scheduledThreadPoolDelegate.getQueue().clear();
        }
    }

    public void clear() {
        log.info("Clear thread pool tasks start");
        for (Map.Entry<String, ThreadPoolDelegate> entry : THREAD_POOLS.entrySet()) {
            ThreadPoolDelegate executor = entry.getValue();
            if (executor != null) {
                executor.clear();
            }
        }
        for (Map.Entry<String, ScheduledThreadPoolDelegate> entry : SCHEDULED_POOLS.entrySet()) {

            ScheduledThreadPoolDelegate executor = entry.getValue();
            if (executor != null) {
                executor.getQueue().clear();
            }
        }
        log.info("Clear thread pool tasks end");
    }


    public void shutdownAll(long timeout, TimeUnit timeUnit) {
        synchronized (this) {
            HashMap<String, ThreadPoolExecutor> map = new HashMap<>(THREAD_POOLS);
            map.putAll(SCHEDULED_POOLS);
            THREAD_POOLS.clear();
            SCHEDULED_POOLS.clear();

            for (Map.Entry<String, ThreadPoolExecutor> entry : map.entrySet()) {
                ThreadPoolExecutor threadPoolExecutor = entry.getValue();
                log.info("thread pool is closing: {}", entry.getKey());
                threadPoolExecutor.shutdown();
                try {
                    if (!threadPoolExecutor.awaitTermination(timeout, timeUnit)) {
                        threadPoolExecutor.shutdownNow();
                    }
                    log.info("thread pool is closed: {}", entry.getKey());
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted while waiting for customThreadPool to be shutdown. key:" + entry.getKey());
                }
            }
        }


    }


    @Override
    public ThreadPoolConfigure getThreadPoolConfig(String key) {
        return POOL_CONFIGURE_MAP.get(key);
    }

    @Override
    public boolean tryAddThreadPoolConfigure(ThreadPoolConfigure configure) {
        if (POOL_CONFIGURE_MAP.containsKey(configure.getKey())) {
            return false;
        }
        POOL_CONFIGURE_MAP.put(configure.getKey(), configure);
        return true;
    }

    @Override
    public Map<String, ThreadPoolConfigure> getAllThreadPoolConfig() {
        return new HashMap<>(POOL_CONFIGURE_MAP);
    }
}
