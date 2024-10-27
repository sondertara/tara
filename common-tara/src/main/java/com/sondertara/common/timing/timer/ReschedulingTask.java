package com.sondertara.common.timing.timer;

import com.sondertara.common.timing.scheduling.SimpleTriggerContext;
import com.sondertara.common.timing.scheduling.Trigger;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author huangxiaohu.1ih
 */
@Slf4j
public class ReschedulingTask implements Timeout, TimerTask {
    private final Timer timer;
    private final Trigger trigger;
    private final SimpleTriggerContext triggerContext = new SimpleTriggerContext();
    private volatile Date scheduledExecutionTime;
    private final TimerTask delegateTask;
    private final Consumer<Throwable> errorHandler;
    private final Object triggerContextMonitor = new Object();

    private volatile int cancelState = -1;
    private volatile long currentExecutionTime = 0;
    private volatile boolean scheduled = false;

    public ReschedulingTask(Timer timer, Runnable task, Trigger trigger, Consumer<Throwable> errorHandler) {
        this(timer, new RunnableToTimerTaskAdapter(Objects.requireNonNull(task, "task is required")), trigger, errorHandler);
    }

    public ReschedulingTask(Timer timer, TimerTask task, Trigger trigger, Consumer<Throwable> errorHandler) {
        Objects.requireNonNull(task, "task must not be null");
        Objects.requireNonNull(errorHandler, "ErrorHandler must not be null");
        this.timer = timer;
        this.delegateTask = task;
        this.trigger = trigger;
        this.errorHandler = errorHandler;
    }


    /**
     * 对象创建完毕后，调用该方法
     *
     * @return 返回timeout
     */
    public Timeout schedule() {
        synchronized (this.triggerContextMonitor) {
            this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
            if (this.scheduledExecutionTime == null) {
                return null;
            }
            System.out.println("调度");
            this.scheduled = true;
            this.currentExecutionTime = this.scheduledExecutionTime.getTime();
            long initialDelay = this.currentExecutionTime - System.currentTimeMillis();
            if (initialDelay < 0) {
                initialDelay = 0L;
            }
            this.timer.newTimeout(this, initialDelay, TimeUnit.MILLISECONDS);
            System.out.println("调度完成");
            return this;
        }
    }


    private void checkState() {
        if (scheduled) {
            return;
        }
        //需要先调用schedule方法
        throw new IllegalStateException("You should call schedule method first.");
    }

    public void run(Timeout timeout) throws Exception {

        checkState();
        Date actualExecutionTime = new Date();
        try {
            this.delegateTask.run(timeout);
        } catch (Throwable ex) {
            this.errorHandler.accept(ex);
        }
        Date completionTime = new Date();
        synchronized (this.triggerContextMonitor) {
            this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
        }

        if (this.isCancelled()) {
            System.out.println("取消了");
        } else {
            System.out.println("没取消");

            schedule();
        }
    }

    @Override
    public Timer timer() {
        return this.timer;
    }

    @Override
    public TimerTask task() {
        checkState();
        return this.delegateTask;
    }

    @Override
    public boolean isExpired() {
        checkState();
        return this.currentExecutionTime - System.currentTimeMillis() <= 0;
    }

    @Override
    public boolean isCancelled() {
        checkState();
        return this.cancelState == 1;
    }

    @Override
    public boolean cancel() {
        checkState();
        this.cancelState = 1;
        return true;
    }


}
