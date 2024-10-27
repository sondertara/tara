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

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 使用Caller-Runs(调用者执行)饱和策略，不抛弃任务，也不抛出异常，而是将当前任务回退到发起这个调用者执行的线程所在的上级线程去执行
 */
public class CallerRunsPolicyWithReport extends ThreadPoolExecutor.CallerRunsPolicy
        implements RejectWarning {
    @Override
    public String poolName() {
        return poolName;
    }

    @Setter
    private static final Logger LOG = LoggerFactory.getLogger(CallerRunsPolicyWithReport.class);

    private String poolName;

    public CallerRunsPolicyWithReport() {
        this(null);
    }

    public CallerRunsPolicyWithReport(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (poolName != null) {
            LOG.error("Thread pool [{}] is exhausted, executor={}", poolName, executor.toString());
        }
        rejectWarning(runnable, executor);

        super.rejectedExecution(runnable, executor);
    }
}
