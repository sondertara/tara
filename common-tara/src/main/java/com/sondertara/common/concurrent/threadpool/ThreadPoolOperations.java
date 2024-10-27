package com.sondertara.common.concurrent.threadpool;

import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface ThreadPoolOperations {

    void resetAll();
    void reset(String key);

    @Nullable
    ThreadPoolConfigure getConfigure(String key);

    boolean contains(String key);

   Map<String,ThreadPoolConfigure> listConfigure();

    boolean registerConfigure(ThreadPoolConfigure configure);



    public void clear(String key);
    public void clearAll();

    void shutdown(String key);
    void shutdown(String key,int timeoutSecs);
    void shutdownAll();
    void shutdownByPrefix(String prefix);


    MetricThreadPoolExecutor defaultPool();



    MetricThreadPoolExecutor createPool(ThreadPoolConfigure configure);

    void refreshPool(ThreadPoolConfigure configure);
    MetricThreadPoolExecutor getPool(String key);
    MetricThreadPoolExecutor getOrCreatePool(ThreadPoolConfigure configure);


}
