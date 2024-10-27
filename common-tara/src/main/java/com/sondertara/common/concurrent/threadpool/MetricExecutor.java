package com.sondertara.common.concurrent.threadpool;

public interface MetricExecutor extends ExecutorAdapter {
    /**
     * reject task count when executor call reject handler
     *
     * @return reject task count
     */
    long getRejectedTaskCount();


    long runTimeoutTaskCount();

    long getRunErrorTaskCount();
}
