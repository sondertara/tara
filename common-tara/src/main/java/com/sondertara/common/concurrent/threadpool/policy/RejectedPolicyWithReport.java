package com.sondertara.common.concurrent.threadpool.policy;

/**
 * Title: Nepxion EventBus
 *
 * <p>Description: Nepxion EventBus AOP
 *
 * <p>Copyright: Copyright (c) 2017-2050
 *
 * <p>Company: Nepxion
 *
 * @author Haojun Ren
 * @version 1.0
 */

import com.sondertara.common.concurrent.runnable.RejectedRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 如果该任务实现了RejectedRunnable接口，那么交给用户去实现拒绝服务的逻辑，否则以FIFO的方式抛弃队列中一部分现有任务，再添加新任务
 *
 * @author huangxiaohu.1ih
 */
public class RejectedPolicyWithReport implements RejectedExecutionHandler, RejectWarning {
    private static final Logger LOG = LoggerFactory.getLogger(RejectedPolicyWithReport.class);

    private final String threadName;

    @Override
    public String poolName() {
        return threadName;
    }

    public RejectedPolicyWithReport() {
        this(Thread.currentThread().getName());
    }

    public RejectedPolicyWithReport(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            LOG.error("Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }
        if (runnable instanceof RejectedRunnable) {
            // 交给用户来处理
            ((RejectedRunnable) runnable).rejected();
        } else {
            if (!executor.isShutdown()) {
                BlockingQueue<Runnable> queue = executor.getQueue();
                if (null != queue) {
                    // 舍弃1/16队列元素，例如10个单位的元素，舍弃1个
                    int discardSize = Math.max(queue.size() >> 4, 1);
                    for (int i = 0; i < discardSize; i++) {
                        // 从头部移除并返问队列头部的元素
                        Runnable poll = queue.poll();
                        rejectWarning(poll, executor);
                    }
                    try {
                        // 添加一个元素， 如果队列满，则阻塞
                        queue.put(runnable);
                    } catch (InterruptedException e) {
                        // should not be interrupted
                    }
                }
            }
        }
    }

}
