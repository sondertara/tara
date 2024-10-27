package com.sondertara.common.concurrent.threadpool;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.sondertara.common.base.Valid;
import com.sondertara.common.concurrent.DefaultThreadFactory;
import com.sondertara.common.concurrent.threadpool.configure.TpExecutorProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * 自定义线程池 获取可以用时间轮来监听是否过期并取消任务
 *
 * @author walter.tan
 */
@Slf4j
public class MetricThreadPoolExecutor extends ThreadPoolExecutor implements MetricExecutor {


    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final String name;

    @Getter
    @Setter
    private long threadTimeout;
    @Getter
    @Setter
    private int showThreadQueueSize;

    private final BlockingQueue<Runnable> queue;

    @Nullable
    private final ThreadPoolExecutor delegate;

    @NonNull
    private final ThreadPoolStatProvider threadPoolStatProvider;


    public MetricThreadPoolExecutor(TpExecutorProperties conf) {
        super(conf.getCorePoolSize(), conf.getMaximumPoolSize(), conf.getKeepAliveTimeSec(), TimeUnit.SECONDS, conf.getQueue(), new DefaultThreadFactory(conf.getKey(), conf.isDaemon()), conf.getRejectedExecutionHandler());
        this.threadTimeout = conf.getRunTimeout();
        this.queue = conf.getQueue();
        this.showThreadQueueSize = conf.getShowQueueWarningSize();
        this.allowCoreThreadTimeOut(conf.isAllowCoreThreadTimeOut());
        this.name = conf.getKey();
        this.showThreadQueueSize = this.queue.remainingCapacity() + queue.size() >> 1;
        this.delegate = null;

        this.threadPoolStatProvider = new ThreadPoolStatProvider(this);

    }

    public MetricThreadPoolExecutor(ThreadPoolExecutor delegate, String name) {
        super(delegate.getCorePoolSize(), delegate.getMaximumPoolSize(), delegate.getKeepAliveTime(TimeUnit.SECONDS), TimeUnit.SECONDS, delegate.getQueue(), delegate.getThreadFactory(), delegate.getRejectedExecutionHandler());
        this.delegate = delegate;
        this.queue = delegate.getQueue();
        this.name = name;
        this.showThreadQueueSize = this.queue.remainingCapacity() + queue.size() >> 1;

        this.threadPoolStatProvider = new ThreadPoolStatProvider(this);
    }


    public static MetricThreadPoolExecutor wrap(ThreadPoolExecutor executor, String name) {
        return new MetricThreadPoolExecutor(executor, name);
    }

    public void incRejectCount(int count) {
        threadPoolStatProvider.incRejectCount(count);
    }

    /**
     * 执行一个无返回值且无超时时间的任务
     * 注：任务内部的异常，会自行被线程的UncaughtExceptionHandler捕获
     * CC
     *
     * @param task
     */
    @Override
    public void execute(@NonNull Runnable task) {
        Runnable ttlRunnable = new TtlRunnableWrapper(task, this);
        if (null == this.delegate) {
            super.execute(ttlRunnable);
        } else {
            this.delegate.execute(ttlRunnable);
        }
        int size = this.queue.size();
        if (this.showThreadQueueSize > 0 && size >= this.showThreadQueueSize) {
            log.info("key<" + this.name + ">" + "task queue length <" + size + ">");
        }
    }

    public void clear() {
        this.queue.clear();
    }

    @Override
    public BlockingQueue<Runnable> getQueue() {
        return this.queue;
    }


    @Override
    public int getQueueSize() {
        return this.getQueue().size();
    }


    @Override
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        final Lock readLock = readWriteLock.readLock();
        try {
            readLock.lockInterruptibly();
            try {
                return super.getRejectedExecutionHandler();
            } finally {
                readLock.unlock();
            }
        } catch (InterruptedException e) {
            return null;
        }
    }


    /**
     * 提交一个可以带有超时限制的含返回值的任务
     * 注：任务内部的异常，不会被线程的UncaughtExceptionHandler捕获，建议选择以下其中一种方式处理：
     * 1. 调用future.get()
     * 2. 在任务内部自行处理异常
     *
     * @param task
     * @param <T>
     * @return the ref
     */
    @Override
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        return doSubmit(task);
    }

    public static class TtlRunnableWrapper implements Runnable {

        private final TtlRunnable runnable;
        private final MetricThreadPoolExecutor metricThreadPoolExecutor;

        public TtlRunnableWrapper(Runnable runnable, MetricThreadPoolExecutor metricThreadPoolExecutor) {
            TtlRunnable ttlRunnable = TtlRunnable.get(runnable, false, true);
            this.metricThreadPoolExecutor = metricThreadPoolExecutor;
            this.runnable = ttlRunnable;
        }

        @Override
        public void run() {
            Throwable thrown = null;
            try {
                metricThreadPoolExecutor.preExecute(Thread.currentThread(), runnable);
                runnable.run();
            } catch (Exception e) {
                thrown = e;
                throw e;
            } finally {
                metricThreadPoolExecutor.postExecute(runnable, thrown);
            }

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !Runnable.class.isAssignableFrom(o.getClass())) {
                return false;
            }
            //同一个类
            if (getClass() == o.getClass()) {
                TtlRunnableWrapper that = (TtlRunnableWrapper) o;
                return runnable.equals(that.runnable);
            } else if (o instanceof TtlRunnable) {
                return runnable.equals(o);
            } else {
                return runnable.unwrap().equals(o);
            }
        }

        @Override
        public int hashCode() {
            return runnable.unwrap().hashCode();
        }

        @Override
        public String toString() {
            return this.getClass().getName() + " - " + runnable.unwrap();
        }
    }

    public static class TtlCallableWrapper<V> implements Callable<V> {

        private final Callable<V> callable;
        private final ThreadPoolStatProvider threadPoolStatProvider;

        public TtlCallableWrapper(Callable<V> task, ThreadPoolStatProvider threadPoolStatProvider) {
            TtlCallable<V> ttlCallable = TtlCallable.get(task, false, true);
            this.threadPoolStatProvider = threadPoolStatProvider;
            this.callable = ttlCallable;
        }

        @Override
        public V call() throws Exception {

            try {
                threadPoolStatProvider.incRunningCount();
                return callable.call();
            } finally {
                threadPoolStatProvider.decRunningCount();
            }
        }
    }


    private <T> Future<T> doSubmit(Callable<T> task) {
        Future<T> future;
        if (null == this.delegate) {
            future = super.submit(task);
        } else {
            future = this.delegate.submit(task);
        }
        int size = this.queue.size();
        if (this.showThreadQueueSize > -1 && size >= this.showThreadQueueSize) {
            log.info("task queue length <" + size + "> key<" + this.name + ">");
        }

        //this.isOvertime(future);
        return future;
    }

    protected void preExecute(Thread t, Runnable r) {
        try {
            this.threadPoolStatProvider.incRunningCount();
            if (threadTimeout > 0) {
                this.threadPoolStatProvider.startRunTimeoutTask(t, r);
            }
        } catch (Exception e) {
            log.error("Thread pool(name={}) set thread run timeout metric error,{}", name, e.getMessage());
        }
    }

    protected void postExecute(Runnable r, Throwable t) {
        threadPoolStatProvider.decRunningCount();
        if (null == t) {
            threadPoolStatProvider.incCompleteTaskCount(1);
        } else {
            threadPoolStatProvider.incRunErrorTaskCount(1);
        }
        if (threadTimeout > 0) {
            try {
                this.threadPoolStatProvider.cancelRunTimeoutTask(r);
            } catch (Exception e) {
                log.error("Thread pool(name={}) remove thread run timeout metric error,{}", name, e.getMessage());
            }
        }
    }

    /**
     * 提交一个可以带有超时限制的不含返回值的任务
     *
     * @param task             待执行的任务
     * @param exceptionHandler 任务内部异常的处理器
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

    @Override
    public long getRejectedTaskCount() {
        return threadPoolStatProvider.getRejectedTaskCount();
    }


    @Override
    public long getRunErrorTaskCount() {
        return this.threadPoolStatProvider.getRunErrorTaskCount();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long runTimeoutTaskCount() {
        return threadPoolStatProvider.getRunTimeoutCount();
    }


    @Override
    public long getTaskCount() {
        return this.threadPoolStatProvider.getTaskCount();
    }

    @Override
    public long getCompletedTaskCount() {
        return threadPoolStatProvider.getCompleteTaskCount();
    }

    @Override
    public int getActiveCount() {
        return this.threadPoolStatProvider.getRunningCount();
    }

    @Override
    public void shutdown() {
        final Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            if (null != this.delegate) {
                delegate.shutdown();
            }
            super.shutdown();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (null == delegate) {
            return super.shutdownNow();
        }
        final Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            List<Runnable> runnableList = delegate.shutdownNow();
            List<Runnable> runnables = super.shutdownNow();
            return runnableList;
        } finally {
            writeLock.unlock();

        }
    }

    @Override
    public boolean isShutdown() {
        return null == this.delegate ? super.isShutdown() : delegate.isShutdown();
    }

    @Override
    public boolean isTerminating() {
        return null == this.delegate ? super.isTerminating() : delegate.isTerminating();
    }

    @Override
    public boolean isTerminated() {
        return null == this.delegate ? super.isTerminated() : delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (null == this.delegate) {
            return super.awaitTermination(timeout, unit);
        }
        final Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            if (null != this.delegate) {
                this.delegate.awaitTermination(timeout, unit);
            }
            return super.awaitTermination(timeout, unit);
        } finally {

            writeLock.unlock();
        }
    }

    @Override
    protected void finalize() {
        shutdown();
    }

    @Override
    public void setThreadFactory(@NonNull ThreadFactory threadFactory) {
        if (null != this.delegate) {
            this.delegate.setThreadFactory(threadFactory);
        }
        super.setThreadFactory(threadFactory);
    }

    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (null != this.delegate) {
            this.delegate.setRejectedExecutionHandler(handler);
        }
        super.setRejectedExecutionHandler(handler);
    }


    @Override
    public void setCorePoolSize(int corePoolSize) {
        if (null != this.delegate) {
            this.delegate.setCorePoolSize(corePoolSize);
        }
        super.setCorePoolSize(corePoolSize);
    }

    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (null != this.delegate) {
            this.delegate.setMaximumPoolSize(maximumPoolSize);
        }
        super.setMaximumPoolSize(maximumPoolSize);
    }


    @Override
    public boolean prestartCoreThread() {
        if (null != this.delegate) {
            return this.delegate.prestartCoreThread();
        } else {
            return super.prestartCoreThread();
        }
    }

    @Override
    public int prestartAllCoreThreads() {
        if (null != this.delegate) {
            return this.delegate.prestartAllCoreThreads();
        }
        return super.prestartAllCoreThreads();
    }


    @Override
    public void allowCoreThreadTimeOut(boolean value) {
        if (this.delegate != null) {
            this.delegate.allowCoreThreadTimeOut(value);
        }
        super.allowCoreThreadTimeOut(value);
    }


    @Override
    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (null != this.delegate) {
            this.delegate.setKeepAliveTime(time, unit);
        }
        super.setKeepAliveTime(time, unit);

    }


    @Override
    public boolean remove(Runnable task) {
        TtlRunnableWrapper wrapper = new TtlRunnableWrapper(task, this);
        if (null != this.delegate) {
            return this.delegate.remove(wrapper);
        }
        return super.remove(wrapper);
    }

    @Override
    public void purge() {
        if (null != this.delegate) {
            this.delegate.purge();
            return;
        }
        super.purge();
    }

    @Override
    public int getPoolSize() {
        return super.getCorePoolSize();
    }

    @Override
    public int getLargestPoolSize() {
        if (null!=delegate){
            return Objects.requireNonNull(delegate).getLargestPoolSize();
        }
        return super.getLargestPoolSize();
    }

    @Override
    public ThreadFactory getThreadFactory() {
        return super.getThreadFactory();
    }

    @Override
    public int getCorePoolSize() {
        return super.getCorePoolSize();
    }

    @Override
    public boolean allowsCoreThreadTimeOut() {
        return super.allowsCoreThreadTimeOut();
    }

    @Override
    public int getMaximumPoolSize() {
        return super.getMaximumPoolSize();
    }

    @Override
    public long getKeepAliveTime(TimeUnit unit) {
        return super.getKeepAliveTime(unit);
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 2, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
        MetricThreadPoolExecutor executor =MetricThreadPoolExecutor.wrap(threadPool,"test");

        LifoThreadPoolExecutor lifoThreadPoolExecutor = new LifoThreadPoolExecutor("tmp", 2, 2, 10, TimeUnit.SECONDS, 100);

        executor.execute(() -> {
            System.out.print("测试1:");
            System.out.println(Thread.currentThread());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executor.execute(() -> {
            System.out.print("测试2:");
            System.out.println(Thread.currentThread());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.execute(() -> {
            System.out.print("测试3:");
            System.out.println(Thread.currentThread());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
//        executor.execute(runnable);
//        executor.execute(runnable);
//        executor.execute(runnable);


        Thread.sleep(10 * 1000);

        Valid.eq(threadPool.getActiveCount(), executor.getActiveCount());
        Valid.eq(threadPool.getPoolSize(), executor.getPoolSize());
        Valid.eq(threadPool.getCorePoolSize(), executor.getCorePoolSize());
        Valid.eq(threadPool.isShutdown(), executor.isShutdown());
        Valid.eq(threadPool.getQueue().size(), executor.getQueue().size());
        System.out.println(threadPool.getQueue().size());
        System.out.println(executor.getQueue().size());
        System.out.println(executor.shutdownNow());


    }

}
