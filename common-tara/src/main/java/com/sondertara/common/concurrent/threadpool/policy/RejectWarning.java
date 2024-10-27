package com.sondertara.common.concurrent.threadpool.policy;

import com.sondertara.common.concurrent.runnable.NamedRunnable;
import com.sondertara.common.concurrent.threadpool.MetricThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author huangxiaohu
 */
public interface RejectWarning {
    Logger LOG = LoggerFactory.getLogger(RejectWarning.class);

    String poolName();

    /**
     * 决绝警告
     *
     * @param runnable 拒绝的线程
     */
    default void rejectWarning(Runnable runnable, ExecutorService executor) {
        if (executor instanceof MetricThreadPoolExecutor) {
            MetricThreadPoolExecutor dtpExecutor = (MetricThreadPoolExecutor) executor;
            dtpExecutor.incRejectCount(1);
        }
        if (null == runnable) {
            return;
        }
        String taskName = runnable.getClass().getName();
        if (runnable instanceof NamedRunnable) {
            NamedRunnable namedRunnable = (NamedRunnable) runnable;
            String namedRunnableName = namedRunnable.getName();
            if (null != namedRunnableName) {
                taskName = taskName + "@" + namedRunnableName;
            }
        }
        LOG.error(
                "Thread pool:[{}] reject task:[{}],class:[{}]",
                poolName(),
                taskName,
                runnable.getClass().getName());
    }
}
