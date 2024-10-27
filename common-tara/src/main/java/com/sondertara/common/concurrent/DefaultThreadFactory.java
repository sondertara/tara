package com.sondertara.common.concurrent;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.id.IndexNumberUtils;
import lombok.Getter;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 线程创建工厂类，此工厂可选配置：
 *
 * <pre>
 * 1. 自定义线程命名前缀
 * 2. 自定义是否守护线程
 * </pre>
 *
 * @author looly
 *  */
public class DefaultThreadFactory implements ThreadFactory {

    /**
     * 命名前缀
     */
    @Getter
    private final String prefix;
    /**
     * 线程组
     */
    @Getter
    private final ThreadGroup group;
    /**
     * 线程组
     */
    @Getter
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * 是否守护线程
     */
    @Getter
    private final boolean isDaemon;
    @Getter
    private final int priority;
    /**
     * 无法捕获的异常统一处理
     */
    @Getter
    private final UncaughtExceptionHandler handler;

    /**
     * 构造
     *
     * @param prefix   线程名前缀
     * @param isDaemon 是否守护线程
     */
    public DefaultThreadFactory(String prefix, boolean isDaemon) {
        this(prefix, null, isDaemon);
    }

    /**
     * 构造
     *
     * @param prefix      线程名前缀
     * @param threadGroup 线程组，可以为null
     * @param isDaemon    是否守护线程
     */
    public DefaultThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon) {
        this(prefix, threadGroup, isDaemon, Thread.NORM_PRIORITY);
    }

    public DefaultThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon, final int priority) {
        this(prefix, threadGroup, isDaemon, priority, null);
    }

    /**
     * 构造
     *
     * @param prefix      线程名前缀
     * @param threadGroup 线程组，可以为null
     * @param isDaemon    是否守护线程
     * @param handler     未捕获异常处理
     */
    public DefaultThreadFactory(String prefix, ThreadGroup threadGroup, boolean isDaemon, final int priority, UncaughtExceptionHandler handler) {
        String pre = StringUtils.isBlank(prefix) ? "Tara" : prefix;
        if (pre.endsWith("-")) {
            pre = prefix.substring(0, prefix.length() - 1);
        }
        this.prefix = pre + '$' + IndexNumberUtils.incrementAndGet(pre) + '-';
        if (null == threadGroup) {
            threadGroup = new ThreadGroup("Tara");
        }
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
        }

        this.group = threadGroup;
        this.isDaemon = isDaemon;
        this.handler = handler;
        this.priority = priority;
    }

    public Thread execute(Runnable r) {
        Thread thread = newThread(r);
        thread.start();
        return thread;
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread t = new Thread(this.group, r, StringUtils.format("{}{}", prefix, threadNumber.getAndIncrement()));

        if (t.isDaemon() != isDaemon) {
            t.setDaemon(isDaemon);
        }
        if (t.getPriority() != priority) {
            t.setPriority(priority);
        }
        //异常处理
        if (null != this.handler) {
            t.setUncaughtExceptionHandler(handler);
        }
        return t;
    }

}
