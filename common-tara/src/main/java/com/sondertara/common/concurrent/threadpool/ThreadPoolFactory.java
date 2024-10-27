package com.sondertara.common.concurrent.threadpool;

import com.sondertara.common.concurrent.threadpool.exceptions.ThreadPoolCreateException;
import com.sondertara.common.concurrent.threadpool.queue.QueueTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author walter.tan
 */
@Slf4j
public class ThreadPoolFactory implements ThreadPoolOperations {

    private static final ThreadPoolFactory INSTANCE = new ThreadPoolFactory();

    private final ThreadPoolManager threadPoolManager = new ThreadPoolManager();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    private ThreadPoolFactory() {
    }


    public static ThreadPoolFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void resetAll() {
        try {
            lock.writeLock().lockInterruptibly();
            threadPoolManager.shutdownAll(10, TimeUnit.MILLISECONDS);
            List<ThreadPoolConfigure> list = threadPoolManager.getAllThreadPoolConfig().values().stream().sorted(Comparator.comparingLong(ThreadPoolConfigure::getCreateTime)).collect(Collectors.toList());

            for (ThreadPoolConfigure configure : list) {
                if (configure.isScheduledPool()) {
                    threadPoolManager.createSchedulePool(configure);
                } else {
                    threadPoolManager.createPool(configure);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean registerConfigure(ThreadPoolConfigure configure) {
        return threadPoolManager.tryAddThreadPoolConfigure(configure);
    }

    @Override
    public Map<String, ThreadPoolConfigure> listConfigure() {
        return threadPoolManager.getAllThreadPoolConfig();
    }

    @Override
    public boolean contains(String key) {
        return null != threadPoolManager.getThreadPoolConfig(key);
    }

    @Override
    public ThreadPoolConfigure getConfigure(String key) {
        return Optional.ofNullable(threadPoolManager.getThreadPoolConfig(key)).map(s -> s.copyTo(ThreadPoolConfigure.class)).orElse(null);
    }

    @Override
    public void reset(String key) {
        ThreadPoolConfigure configure = threadPoolManager.getThreadPoolConfig(key);
        if (null == configure) {
            return;
        }
        threadPoolManager.shutdown(configure.getKey());
        if (configure.isScheduledPool()) {
            threadPoolManager.createSchedulePool(configure);
        } else {
            threadPoolManager.createPool(configure);
        }
    }


    @Override
    public MetricThreadPoolExecutor defaultPool() {
        //使用线程池工具,使用默自定义线程池类型
        ThreadPoolConfigure conf = threadPoolManager.getDefaultThreadPoolConfig();
        return this.getOrCreatePool(conf);
    }


    public ScheduledExecutorService getScheduledPool(String key) {

        //使用线程池工具,使用自定义线程池类型
        ThreadPoolConfigure conf = threadPoolManager.getThreadPoolConfig(key);
        if (null == conf) {
            throw new IllegalArgumentException("No thread pool conf found by-> " + key);
        }
        conf.setScheduledPool(true);
        return threadPoolManager.createSchedulePool(conf);
    }

    @Override
    public MetricThreadPoolExecutor getPool(String key) {
        //使用线程池工具,使用自定义线程池类型
        ThreadPoolConfigure conf = threadPoolManager.getThreadPoolConfig(key);
        if (null == conf) {
            throw new IllegalArgumentException("No thread pool conf found by-> " + key);
        }
        return this.getOrCreatePool(conf);
    }

    @Override
    public MetricThreadPoolExecutor getOrCreatePool(ThreadPoolConfigure configure) {
        try {
            lock.readLock().lockInterruptibly();
            return threadPoolManager.getOrCreatePool(configure);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void refreshPool(ThreadPoolConfigure configure) {
        try {
            lock.readLock().lockInterruptibly();
            threadPoolManager.refreshPool(configure);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public MetricThreadPoolExecutor createPool(ThreadPoolConfigure configure) {
        if (threadPoolManager.tryAddThreadPoolConfigure(configure)) {
            return threadPoolManager.getOrCreatePool(configure);
        }
        throw new ThreadPoolCreateException("Thread pool(name={}) is exist", configure.getKey());
    }

    public MetricThreadPoolExecutor newSingleThreadExecutor(String poolName) {
        return newSingleThreadExecutor(poolName, false);

    }

    public MetricThreadPoolExecutor newSingleThreadExecutor(String poolName, boolean daemon) {
        threadPoolManager.tryAddThreadPoolConfigure(ThreadPoolConfigure.builder().key(poolName).corePoolSize(1).daemon(daemon).maxPoolSize(1).build());
        return threadPoolManager.getOrCreatePool(threadPoolManager.getThreadPoolConfig(poolName));

    }


    public ScheduledExecutorService newScheduledThreadPool(String poolName, int corePoolSize) {

        threadPoolManager.tryAddThreadPoolConfigure(ThreadPoolConfigure.builder().key(poolName).corePoolSize(corePoolSize).maxPoolSize(corePoolSize).build());

        return threadPoolManager.createSchedulePool(threadPoolManager.getThreadPoolConfig(poolName));


    }


    public MetricThreadPoolExecutor newFixedThreadPool(String poolName, int nThreads, int queueSize) {

        threadPoolManager.tryAddThreadPoolConfigure(ThreadPoolConfigure.builder().key(poolName).corePoolSize(nThreads).queue(QueueTypeEnum.VARIABLE_LINKED_BLOCKING_QUEUE, queueSize).maxPoolSize(nThreads * 2).build());
        return threadPoolManager.getOrCreatePool(threadPoolManager.getThreadPoolConfig(poolName));


    }


    public MetricThreadPoolExecutor newFixedThreadPool(String poolName, int nThreads) {
        threadPoolManager.tryAddThreadPoolConfigure(ThreadPoolConfigure.builder().key(poolName).corePoolSize(nThreads).maxPoolSize(nThreads).build());
        return threadPoolManager.createPool(threadPoolManager.getThreadPoolConfig(poolName));
    }

    @Override
    public void shutdownByPrefix(String prefix) {
        try {
            lock.writeLock().lockInterruptibly();
            List<String> list = threadPoolManager.getAllThreadPoolConfig().keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList());
            for (String poolName : list) {
                threadPoolManager.shutdown(poolName);
            }
            log.info("ThreadPoolFactory has shutdown  thread pools with prefix({})", prefix);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void shutdownAll() {
        try {
            lock.writeLock().lockInterruptibly();
            threadPoolManager.shutdownAll(10, TimeUnit.SECONDS);
            log.info("ThreadPoolFactory has shutdown all thread pools.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public synchronized void shutdown(String key, int timeoutSecs) {
        try {
            lock.writeLock().lockInterruptibly();
            threadPoolManager.shutdown(key, timeoutSecs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public synchronized void shutdown(String poolName) {
        shutdown(poolName, 1);
    }

    @Override
    public void clear(String poolName) {
        threadPoolManager.clear(poolName);
    }


    @Override
    public void clearAll() {
        try {
            lock.readLock().lockInterruptibly();
            threadPoolManager.clear();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }
}
