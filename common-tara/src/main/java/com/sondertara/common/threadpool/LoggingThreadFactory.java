package com.sondertara.common.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可自定义命名线程，并定义{@link Thread.UncaughtExceptionHandler}的实现：进行logger.error的输出。
 * @author walter.tan
 */
@Slf4j
public class LoggingThreadFactory implements ThreadFactory {

    private final AtomicInteger threadCreationCounter = new AtomicInteger();
    private final String name;

    public LoggingThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        int threadNumber = threadCreationCounter.incrementAndGet();
        Thread workerThread = new Thread(r, name + "-" + threadNumber);
        workerThread.setUncaughtExceptionHandler((thread, e) -> log.error("Thread {}", thread.getName(), e));
        return workerThread;
    }
}
