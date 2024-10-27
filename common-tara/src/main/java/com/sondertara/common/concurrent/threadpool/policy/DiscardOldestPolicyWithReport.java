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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务饱和时以FIFO的方式抛弃队列中一部分现有任务，再添加新任务
 */
public class DiscardOldestPolicyWithReport extends ThreadPoolExecutor.DiscardOldestPolicy implements RejectWarning {
    private static final Logger LOG = LoggerFactory.getLogger(DiscardOldestPolicyWithReport.class);

    private String poolName;

    public DiscardOldestPolicyWithReport() {
        this(null);
    }

    public DiscardOldestPolicyWithReport(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (poolName != null) {
            LOG.error("Thread pool [{}] is exhausted, executor={}", poolName, executor.toString());
        }

        if (!executor.isShutdown()) {
            BlockingQueue<Runnable> queue = executor.getQueue();
            // 舍弃1/2队列元素，例如7个单位的元素，舍弃3个
            int discardSize = queue.size() >> 1;
            for (int i = 0; i < discardSize; i++) {
                // 从头部移除并返问队列头部的元素
                Runnable poll = queue.poll();
                rejectWarning(poll, executor);
            }

            // 添加元素，如果队列满，不阻塞，返回false
            if (!queue.offer(runnable)) {
                rejectWarning(runnable, executor);
                if (runnable instanceof Future) {
                    ((Future<?>) runnable).cancel(false);
                }
            }

//            executor.execute(executor.getQueue().poll());
        }
//        rejectWarning(runnable, executor);
//        super.rejectedExecution(runnable, executor);
//        if (runnable instanceof Future) {
//            ((Future<?>) runnable).cancel(false);
//        }

    }

    @Override
    public String poolName() {
        return poolName;
    }
}
