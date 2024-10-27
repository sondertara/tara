package com.sondertara.common.timing.scheduling;

import com.sondertara.common.exception.ErrorHandler;
import com.sondertara.common.timing.timer.ReschedulingTask;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @see ReschedulingTask
 *  */
public class ReschedulingRunnable implements ScheduledFuture<Object>, Runnable {

    private final Trigger trigger;

    private final SimpleTriggerContext triggerContext = new SimpleTriggerContext();
    private ScheduledFuture currentFuture;
    private ScheduledExecutorService executor;
    private Date scheduledExecutionTime;
    private final Runnable delegateTask;
    private final ErrorHandler errorHandler;

    private final Object triggerContextMonitor = new Object();

    public ReschedulingRunnable(ScheduledExecutorService executor, Runnable delegate, Trigger trigger, ErrorHandler errorHandler) {
        Objects.requireNonNull(delegate, "Delegate must not be null");
        Objects.requireNonNull(errorHandler, "ErrorHandler must not be null");
        this.delegateTask = delegate;
        this.errorHandler = errorHandler;
        this.trigger = trigger;
        this.executor = executor;
    }

    public ReschedulingRunnable(Runnable delegate, Trigger trigger, ScheduledExecutorService executor, ErrorHandler errorHandler) {
        this(executor, delegate, trigger, errorHandler);
    }


    public ScheduledFuture schedule() {
        if(this.executor.isShutdown() || this.executor.isTerminated()){
            return null;
        }
        synchronized (this.triggerContextMonitor) {
            this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
            if (this.scheduledExecutionTime == null) {
                return null;
            }
            long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
            if(initialDelay<0){
                initialDelay = 0L;
            }
            this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
            return this;
        }
    }

    @Override
    public void run() {
        Date actualExecutionTime = new Date();
        try {
            this.delegateTask.run();
        } catch (Throwable ex) {
            this.errorHandler.accept(ex);
        }
        Date completionTime = new Date();
        synchronized (this.triggerContextMonitor) {
            this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
        }
        if (!this.currentFuture.isCancelled()) {
            schedule();
        }
    }


    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.currentFuture.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return this.currentFuture.isCancelled();
    }

    public boolean isDone() {
        return this.currentFuture.isDone();
    }

    public Object get() throws InterruptedException, ExecutionException {
        return this.currentFuture.get();
    }

    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.currentFuture.get(timeout, unit);
    }

    public long getDelay(TimeUnit unit) {
        return this.currentFuture.getDelay(unit);
    }

    public int compareTo(Delayed other) {
        if (this == other) {
            return 0;
        }
        long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        return (diff == 0 ? 0 : ((diff < 0) ? -1 : 1));
    }

}
