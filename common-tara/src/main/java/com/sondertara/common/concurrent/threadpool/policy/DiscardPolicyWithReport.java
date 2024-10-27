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

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务饱和时以FIFO的方式抛弃队列中一部分现有任务，再添加新任务
 */
public class DiscardPolicyWithReport extends ThreadPoolExecutor.DiscardPolicy implements RejectWarning {
    private static final Logger LOG = LoggerFactory.getLogger(DiscardPolicyWithReport.class);

    @Override
    public String poolName() {
        return poolName;
    }

    private final String poolName;

    public DiscardPolicyWithReport() {
        this(null);
    }

    public DiscardPolicyWithReport(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (poolName != null) {
            LOG.error("Thread pool [{}] is exhausted, executor={}", poolName, executor.toString());
        }
        if (runnable instanceof Future) {
            ((Future<?>) runnable).cancel(false);
        }
        rejectWarning(runnable, executor);
    }
}
