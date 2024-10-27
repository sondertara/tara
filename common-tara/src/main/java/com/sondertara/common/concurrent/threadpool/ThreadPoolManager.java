package com.sondertara.common.concurrent.threadpool;

import com.sondertara.common.bean.BeanUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.concurrent.ThreadUtils;
import com.sondertara.common.concurrent.threadpool.configure.TpExecutorProperties;
import com.sondertara.common.concurrent.threadpool.queue.VariableLinkedBlockingQueue;
import com.sondertara.common.equator.DiffFieldInfo;
import com.sondertara.common.equator.Equator;
import com.sondertara.common.equator.GetterBaseEquator;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 线程池管理类
 *
 * @author walter.tan
 */
@Slf4j
class ThreadPoolManager implements ConfigureContext {

    private final static Equator EQUATOR = new GetterBaseEquator();

    private static final ConcurrentHashMap<String, MetricThreadPoolExecutor> THREAD_POOLS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ScheduledThreadPoolDelegate> SCHEDULED_POOLS = new ConcurrentHashMap<>();

    private static final Map<String, ThreadPoolConfigure> POOL_CONFIGURE_MAP = Maps.newConcurrentMap();

    ThreadPoolManager() {
    }


    public ScheduledExecutorService createSchedulePool(ThreadPoolConfigure conf) {
        conf.setScheduledPool(true);
        POOL_CONFIGURE_MAP.computeIfAbsent(conf.getKey(), key -> {
            conf.setCreateTime(System.currentTimeMillis());
            return conf;
        });
        return SCHEDULED_POOLS.computeIfAbsent(conf.getKey(), key -> new ScheduledThreadPoolDelegate(conf.copyTo(TpExecutorProperties.class)));
    }


    public static Map<String, MetricThreadPoolExecutor> allThreadPool() {
        return THREAD_POOLS;
    }


    public void shutdown(String type) {
        shutdown(type,1);
    }

    public void shutdown(String type,int timeoutSecs) {
        if (null == POOL_CONFIGURE_MAP.remove(type)) {
            return;
        }
        ExecutorService pool = THREAD_POOLS.get(type);
        if (null != pool) {
            ThreadUtils.shutdownThreadPool(pool,timeoutSecs);
            THREAD_POOLS.remove(type);
            log.warn("Thread Pool(name={}) is shutdown", type);
        }
        ScheduledThreadPoolDelegate poolDelegate = SCHEDULED_POOLS.get(type);
        if (null != poolDelegate) {
            ThreadUtils.shutdownThreadPool(poolDelegate,timeoutSecs);
            SCHEDULED_POOLS.remove(type);
            log.warn("Thread Pool(name={}) is shutdown", type);
        }
    }

    public void clear(String name) {
        log.info("Clear thread pool tasks start for name[{}]", name);
        MetricThreadPoolExecutor threadPoolExecutor = THREAD_POOLS.get(name);
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
        for (Map.Entry<String, MetricThreadPoolExecutor> entry : THREAD_POOLS.entrySet()) {
            MetricThreadPoolExecutor executor = entry.getValue();
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
            HashMap<String, ExecutorService> map = new HashMap<>(THREAD_POOLS);
            map.putAll(SCHEDULED_POOLS);
            THREAD_POOLS.clear();
            SCHEDULED_POOLS.clear();
            POOL_CONFIGURE_MAP.clear();

            for (Map.Entry<String, ExecutorService> entry : map.entrySet()) {
                ExecutorService threadPoolExecutor = entry.getValue();
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
    public void refreshPool(ThreadPoolConfigure conf) {
        ThreadPoolConfigure configure = POOL_CONFIGURE_MAP.get(conf.getKey());
        if (null == configure) {
            return;
        }
        ThreadPoolConfigure copy = configure.copyTo(ThreadPoolConfigure.class);
        BeanUtils.copyNonNullProperties(conf, copy);
        long createTime = configure.getCreateTime();
        copy.setCreateTime(createTime);
        List<DiffFieldInfo> list = EQUATOR.getDiffFields(copy, conf);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        log.warn("Thread Pool(name={}) is refreshing,diff properties:{}", conf.getKey(), list);
        MetricThreadPoolExecutor poolDelegate = THREAD_POOLS.get(conf.getKey());
        Map<String, DiffFieldInfo> diffs = list.stream().collect(Collectors.toMap(DiffFieldInfo::getFieldName, Function.identity()));
        doRefreshPoolSize(poolDelegate, diffs);
        updateQueueProps(poolDelegate, diffs);
    }

    /**
     * Why does it seem so complicated to handle this?
     * Although JDK9 solves this bug, we need to ensure that corePoolSize is less than or equal to maximumPoolSize,
     * otherwise an IllegalArgumentException will be thrown
     *
     * @param executor the executor
     * @see <a href="https://bugs.openjdk.org/browse/JDK-7153400">JDK-7153400</a>
     */
    private static void doRefreshPoolSize(MetricThreadPoolExecutor executor, Map<String, DiffFieldInfo> diffs) {

        DiffFieldInfo corePoolSize = diffs.get("corePoolSize");
        DiffFieldInfo maxCoreSize = diffs.get("maxPoolSize");


        if (null != maxCoreSize) {
            int newMaxCoreSize = (int) maxCoreSize.getSecondVal();
            int oldMaxCoreSize = (int) maxCoreSize.getFirstVal();
            if (newMaxCoreSize < oldMaxCoreSize) {
                if (null != corePoolSize) {
                    executor.setCorePoolSize((Integer) corePoolSize.getSecondVal());
                }
                executor.setMaximumPoolSize(newMaxCoreSize);
                return;
            }
            executor.setMaximumPoolSize(newMaxCoreSize);
        }
        if (null != corePoolSize) {
            executor.setCorePoolSize((Integer) corePoolSize.getSecondVal());
        }
    }

    private static void updateQueueProps(MetricThreadPoolExecutor executor, Map<String, DiffFieldInfo> diffs) {

        DiffFieldInfo queueSize = diffs.get("queueCapacity");
        if (null == queueSize) {
            return;
        }
        int newSize = (int) queueSize.getSecondVal();
        BlockingQueue<Runnable> blockingQueue = executor.getQueue();
        if (blockingQueue instanceof VariableLinkedBlockingQueue) {
            ((VariableLinkedBlockingQueue<Runnable>) blockingQueue).setCapacity(newSize);
            executor.onRefreshQueueCapacity(newSize);

            return;
        }
        log.warn("Thread pool refresh, the blockingqueue capacity cannot be reset, poolName: {}, queueType {}",
                executor.getName(), blockingQueue.getClass().getSimpleName());
    }

    @Override
    public MetricThreadPoolExecutor createPool(ThreadPoolConfigure conf) {
        POOL_CONFIGURE_MAP.put(conf.getKey(), conf);
        THREAD_POOLS.put(conf.getKey(), new MetricThreadPoolExecutor(conf.copyTo(TpExecutorProperties.class)));
        return THREAD_POOLS.get(conf.getKey());
    }


    @Override
    public MetricThreadPoolExecutor getOrCreatePool(ThreadPoolConfigure conf) {
        POOL_CONFIGURE_MAP.computeIfAbsent(conf.getKey(), key -> {
            conf.setCreateTime(System.currentTimeMillis());
            return conf;
        });
        return THREAD_POOLS.computeIfAbsent(conf.getKey(), key -> {

            TpExecutorProperties copied = conf.copyTo(TpExecutorProperties.class);
            return new MetricThreadPoolExecutor(copied);
        });
    }

    @Override
    public ThreadPoolConfigure getThreadPoolConfig(String key) {
        return POOL_CONFIGURE_MAP.get(key);
    }

    @Override
    public boolean tryAddThreadPoolConfigure(ThreadPoolConfigure configure) {
        return null == POOL_CONFIGURE_MAP.putIfAbsent(configure.getKey(), configure);
    }

    @Override
    public Map<String, ThreadPoolConfigure> getAllThreadPoolConfig() {
        return new LinkedHashMap<>(POOL_CONFIGURE_MAP);
    }
}
