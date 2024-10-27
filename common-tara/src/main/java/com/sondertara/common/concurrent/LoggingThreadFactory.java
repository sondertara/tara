package com.sondertara.common.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可自定义命名线程，并定义{@link Thread.UncaughtExceptionHandler}的实现：进行logger.error的输出。
 *
 * @author walter.tan
 */
@Slf4j
public class LoggingThreadFactory extends DefaultThreadFactory {

    private final AtomicInteger threadCreationCounter = new AtomicInteger();


    public LoggingThreadFactory(String prefix, boolean isDaemon) {
        super(prefix, null, isDaemon, Thread.NORM_PRIORITY,(thread, e) -> log.error("Thread {}", thread.getName(), e));
    }

    public LoggingThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon) {
        super(prefix, threadGroup, isDaemon,  Thread.NORM_PRIORITY,(thread, e) -> log.error("Thread {}", thread.getName(), e));
    }


}
