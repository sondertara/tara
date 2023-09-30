package com.sondertara.common.threadpool;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置上下文
 * @author walter.tan
 */
public interface ConfigureContext {


    ThreadPoolExecutor createThreadPool(ThreadPoolConfigure conf);
    /**
     * 返回默认线程池的配置信息
     * @return
     */
    default ThreadPoolConfigure getDefaultThreadPoolConfig(){
        return getThreadPoolConfig("TARA-POOL");
    }

    /**
     * 返回线程池的配置信息
     * @param key 线程池key
     * @return
     */
    ThreadPoolConfigure getThreadPoolConfig(String key);

    /**
     * 尝试添加线程池配置
     * @param configure
     * @return true: 添加成功，false：添加失败
     */
    boolean tryAddThreadPoolConfigure(ThreadPoolConfigure configure);

    /**
     * 返回配置信息集合
     * @return
     */
    default Map<String, ThreadPoolConfigure> getAllThreadPoolConfig(){
        throw new RuntimeException("not allow access because it is unsafe");
    }
}
