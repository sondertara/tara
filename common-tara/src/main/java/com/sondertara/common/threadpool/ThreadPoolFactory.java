package com.sondertara.common.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author walter.tan
 */
@Slf4j
public class ThreadPoolFactory implements ThreadPoolOperations {

    private static final ThreadPoolFactory INSTANCE = new ThreadPoolFactory();

    private final ThreadPoolManager threadPoolManager = new ThreadPoolManager();


    private ThreadPoolFactory() {
        throw new IllegalStateException("Please use singleton");
    }


    public static ThreadPoolFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void resetAll() {
        threadPoolManager.shutdownAll(10, TimeUnit.MILLISECONDS);

        List<ThreadPoolConfigure> list = threadPoolManager.getAllThreadPoolConfig().values().stream().sorted((o1, o2) -> {
            if (o1.getCreateTime() < o2.getCreateTime()) {
                return -1;
            }
            if (o1.getCreateTime() > o2.getCreateTime()) {
                return 1;
            }
            if (o1.getCreateTime() == o2.getCreateTime()) {
                return 0;
            }
            return 0;
        }).collect(Collectors.toList());

        for (ThreadPoolConfigure configure : list) {
            if (configure.isScheduledPool()) {
                threadPoolManager.createSchedulePool(configure);
            } else {
                threadPoolManager.createThreadPool(configure);
            }
        }
    }

    @Override
    public synchronized void reset(String key) {
        threadPoolManager.shutdown(key);

        ThreadPoolConfigure configure = threadPoolManager.getThreadPoolConfig(key);
        if (configure.isScheduledPool()) {
            threadPoolManager.createSchedulePool(configure);
        } else {
            threadPoolManager.createThreadPool(configure);
        }
    }


    @Override
    public ThreadPoolExecutor defaultPool() {
        //使用线程池工具,使用默自定义线程池类型
        ThreadPoolConfigure conf = threadPoolManager.getDefaultThreadPoolConfig();
        return threadPoolManager.createThreadPool(conf);
    }

    public synchronized ThreadPoolExecutor getThreadPool(String key) {
        //使用线程池工具,使用自定义线程池类型
        ThreadPoolConfigure conf = threadPoolManager.getThreadPoolConfig(key);
        if (null == conf) {
            throw new IllegalArgumentException("No thread pool conf found by-> " + key);
        }
        return threadPoolManager.createThreadPool(conf);
    }

    public synchronized ScheduledExecutorService getScheduledPool(String key) {

        //使用线程池工具,使用自定义线程池类型
        ThreadPoolConfigure conf = threadPoolManager.getThreadPoolConfig(key);
        if (null == conf) {
            throw new IllegalArgumentException("No thread pool conf found by-> " + key);
        }
        conf.setScheduledPool(true);
        return threadPoolManager.createSchedulePool(conf);
    }

    public synchronized ThreadPoolExecutor buildPool(ThreadPoolConfigure configure) {
        return threadPoolManager.createThreadPool(configure);
    }

    public synchronized ThreadPoolExecutor newSingleThreadExecutor(String poolName) {
        threadPoolManager.tryAddThreadPoolConfigure(ThreadPoolConfigure.builder().key(poolName).corePoolSize(1).maxPoolSize(1).build());
        return threadPoolManager.createThreadPool(threadPoolManager.getThreadPoolConfig(poolName));
    }


    public synchronized ScheduledExecutorService newScheduledThreadPool(String poolName, int corePoolSize) {
        threadPoolManager.tryAddThreadPoolConfigure(ThreadPoolConfigure.builder().key(poolName).corePoolSize(corePoolSize).maxPoolSize(corePoolSize).build());

        return threadPoolManager.createSchedulePool(threadPoolManager.getThreadPoolConfig(poolName));

    }

    public synchronized ThreadPoolExecutor newFixedThreadPool(String poolName, int nThreads) {
        threadPoolManager.tryAddThreadPoolConfigure(ThreadPoolConfigure.builder().key(poolName).corePoolSize(nThreads).maxPoolSize(nThreads).build());
        return threadPoolManager.createThreadPool(threadPoolManager.getThreadPoolConfig(poolName));

    }

    @Override
    public void shutdownAll() {
        synchronized (this) {
            threadPoolManager.shutdownAll(10, TimeUnit.SECONDS);
            log.info("ThreadPoolFactory has shutdown all thread pools.");
        }
    }

    @Override

    public void shutdown(String poolName) {
        synchronized (this) {
            threadPoolManager.shutdown(poolName);
        }
    }


    @Override
    public void clear(String poolName) {
        threadPoolManager.clear(poolName);

    }


    public void clearAll() {
        threadPoolManager.clear();
    }
}
