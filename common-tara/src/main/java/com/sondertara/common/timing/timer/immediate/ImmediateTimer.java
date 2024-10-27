package com.sondertara.common.timing.timer.immediate;

import com.sondertara.common.timing.timer.AbstractTimer;
import com.sondertara.common.timing.timer.RunnableToTimerTaskAdapter;
import com.sondertara.common.timing.timer.Timeout;
import com.sondertara.common.timing.timer.TimerTask;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 *  */
public class ImmediateTimer extends AbstractTimer {
    public ImmediateTimer() {
        this.running = true;
    }

    public ImmediateTimer(Executor e) {
        this();
        setTaskExecutor(e);
    }

    @Override
    public Timeout newTimeout(Runnable task, long delay, TimeUnit unit) {
        return newTimeout(new RunnableToTimerTaskAdapter(task), delay, unit);
    }

    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        long deadline = System.currentTimeMillis();
        final ImmediateTimeout timeout = new ImmediateTimeout(this, task, deadline);
        timeout.executeTask();
        return timeout;
    }

    @Override
    public Set<Timeout> stop() {
        this.running = false;
        return null;
    }


}
