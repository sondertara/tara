package com.sondertara.common.concurrent.threadpool.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务饱和时, 抛弃任务，抛出异常
 */

public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy implements RejectWarning {
    private static final Logger LOG = LoggerFactory.getLogger(AbortPolicyWithReport.class);

    private  String poolName;

    public AbortPolicyWithReport() {
        this(null);
    }

    public AbortPolicyWithReport(String poolName) {
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

    @Override
    public String poolName() {
        return poolName;
    }
}
