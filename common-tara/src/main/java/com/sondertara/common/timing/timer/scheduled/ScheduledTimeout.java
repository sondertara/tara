package com.sondertara.common.timing.timer.scheduled;

import com.sondertara.common.timing.timer.AbstractTimeout;
import com.sondertara.common.timing.timer.TimerTask;

import java.util.concurrent.ScheduledFuture;

/**
 *  */
public class ScheduledTimeout extends AbstractTimeout {
    private ScheduledFuture future;

    public ScheduledTimeout(ScheduledExecutorTimer timer, TimerTask timerTask, long deadline) {
        super(timer, timerTask, deadline);
    }

    public void setFuture(ScheduledFuture future) {
        this.future = future;
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public void executeTask() {
        if (!compareAndSetState(ST_INIT, ST_EXPIRED)) {
            return;
        }
        super.executeTask();
    }

    @Override
    public boolean cancel() {
        return future.cancel(true);
    }
}
