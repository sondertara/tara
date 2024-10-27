package com.sondertara.common.timing.timer.scheduled;

import com.sondertara.common.timing.timer.AbstractTimer;
import com.sondertara.common.timing.timer.RunnableToTimerTaskAdapter;
import com.sondertara.common.timing.timer.Timeout;
import com.sondertara.common.timing.timer.TimerTask;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *  */
public class ScheduledExecutorTimer extends AbstractTimer {
    private ScheduledExecutorService scheduledExecutor;

    public ScheduledExecutorTimer(ScheduledExecutorService scheduledExecutor, Executor taskExecutor) {
        this.scheduledExecutor = scheduledExecutor;
        setTaskExecutor(taskExecutor);
        this.running = true;
    }

    public ScheduledExecutorTimer(ScheduledExecutorService scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
        setTaskExecutor(taskExecutor);
        this.running = true;
    }


    @Override
    public Timeout newTimeout(Runnable task, long delay, TimeUnit unit) {
        Objects.requireNonNull(task, "task is required");
        TimerTask tt = new RunnableToTimerTaskAdapter(task);
        return this.newTimeout(tt, delay, unit);
    }

    @Override
    public Timeout newTimeout(@NonNull final TimerTask task, long delay, @NonNull TimeUnit unit) {
        if (task == null || unit == null) {
            throw new NullPointerException();
        }
        if (!running) {
            throw new IllegalStateException("Timer is stopped");
        }
        if (delay < 0) {
            delay = 0;
        }

        long deadlineInMills = System.currentTimeMillis() + unit.toMillis(delay);

        final ScheduledTimeout timeout = new ScheduledTimeout(this, task, deadlineInMills);
        ScheduledFuture scheduledFuture = this.scheduledExecutor.schedule(timeout::executeTask, delay, unit);
        timeout.setFuture(scheduledFuture);
        return timeout;
    }


    @Override
    public Set<Timeout> stop() {
        this.running = false;
        scheduledExecutor.shutdown();
        return new HashSet<>();
    }


}
