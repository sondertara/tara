package com.sondertara.common.threadpool;

import java.util.concurrent.ThreadPoolExecutor;

public interface ThreadPoolOperations {

    void resetAll();
    void reset(String key);


    public void clear(String key);
    public void clearAll();

    void shutdown(String key);
    void shutdownAll();


    ThreadPoolExecutor defaultPool();



    ThreadPoolExecutor  buildPool(ThreadPoolConfigure configure);


}
