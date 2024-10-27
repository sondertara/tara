package com.sondertara.common.concurrent.threadpool.aware;

import com.sondertara.common.concurrent.threadpool.ThreadPoolStatProvider;
import com.sondertara.common.concurrent.runnable.NamedRunnable;
import com.sondertara.common.timing.timer.RunnableToTimerTaskAdapter;
import com.sondertara.common.timing.timer.Timeout;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadTimeoutTask extends RunnableToTimerTaskAdapter {

    private final ThreadPoolStatProvider threadPoolStatProvider;


    public ThreadTimeoutTask(ThreadPoolStatProvider threadPoolStatProvider, Runnable runnable) {
        super(runnable);
        this.threadPoolStatProvider = threadPoolStatProvider;
    }


    @Override
    public void run(Timeout timeout) throws Exception {
        if (!timeout.isCancelled()) {
            this.threadPoolStatProvider.incRunTimeoutCount(1);
            String name;
            if (runnable instanceof NamedRunnable) {
                NamedRunnable r = (NamedRunnable) runnable;
                name = r.getName();

            } else {
                name = runnable.getClass().getName();
            }
            log.warn("Thread pool runnable(name={}) execution is timeout", name);

        }

    }
}
