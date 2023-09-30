package com.sondertara.common.threadpool;

import com.sondertara.common.util.StringUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 自定义线程池
 *
 * @author walter.tan
 */
@Slf4j
public class ThreadPoolDelegate extends ThreadPoolExecutor {

    @Getter
    private String key;
    private long threadTimeout;
    private int showThreadQueueSize;

    private BlockingQueue<Runnable> queue;
    private ScheduledExecutorService scdTaskPool;

    ThreadPoolDelegate(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public ThreadPoolDelegate(ThreadPoolConfigure conf) {
        this(conf.getCorePoolSize(), conf.getMaxPoolSize(), conf.getKeepAliveTime(), TimeUnit.SECONDS, conf.getQueue(), new DefaultThreadFactory(conf.getKey()), conf.getRejectedExecutionHandler());
        this.key = conf.getKey();
        this.threadTimeout = conf.getThreadTimeout();
        this.queue = conf.getQueue();
        this.key = !StringUtils.hasText(conf.getKey()) ? this.key : conf.getKey();
        this.threadTimeout = conf.getThreadTimeout() == 0L ? this.threadTimeout : conf.getThreadTimeout();
        this.showThreadQueueSize = conf.getShowThreadQueueSize();
        this.scdTaskPool = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory("CheckPool"));

    }


    /**
     * 执行一个无返回值且无超时时间的任务
     * 注：任务内部的异常，会自行被线程的UncaughtExceptionHandler捕获
     * CC
     *
     * @param task
     */
    @Override
    public void execute(Runnable task) {
        super.execute(task);
        int size = this.queue.size();
        if (this.showThreadQueueSize > -1 && size >= this.showThreadQueueSize) {
            log.info("task queue length <" + size + "> key<" + this.key + ">");
        }
    }


    public void clear() {
        this.queue.clear();
    }

    /**
     * 提交一个可以带有超时限制的含返回值的任务
     * 注：任务内部的异常，不会被线程的UncaughtExceptionHandler捕获，建议选择以下其中一种方式处理：
     * 1. 调用future.get()
     * 2. 在任务内部自行处理异常
     *
     * @param task
     * @param <T>
     * @return
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return doSubmit(task);
    }

    private <T> Future<T> doSubmit(Callable<T> task) {
        Future<T> future = super.submit(task);
        int size = this.queue.size();
        if (this.showThreadQueueSize > -1 && size >= this.showThreadQueueSize) {
            log.info("task queue length <" + size + "> key<" + this.key + ">");
        }

        this.isOvertime(future);
        return future;
    }


    /**
     * 提交一个可以带有超时限制的不含返回值的任务
     *
     * @param task             待执行的任务
     * @param exceptionHandler 任务内部异常的处理器
     * @return
     */
    public void submit(Runnable task, Consumer<Exception> exceptionHandler) {
        try {
            this.doSubmit((Callable<Void>) () -> {
                task.run();
                return null;
            });
        } catch (Exception ex) {
            exceptionHandler.accept(ex);
        }
    }


    private void isOvertime(final Future future) {
        if (this.threadTimeout > 0L) {
            this.scdTaskPool.schedule(() -> {
                if (!future.isDone()) {
                    future.cancel(true);
                    log.warn("task cancel because out of time: over <" + ThreadPoolDelegate.this.threadTimeout + "ms> key<" + ThreadPoolDelegate.this.key + ">");
                }
            }, this.threadTimeout, TimeUnit.MILLISECONDS);
        }

    }

}
