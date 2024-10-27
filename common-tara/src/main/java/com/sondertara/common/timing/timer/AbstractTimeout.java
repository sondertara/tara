package com.sondertara.common.timing.timer;

import com.sondertara.common.concurrent.ImmediateExecutor;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author huangxiaohu.1ih
 */
public abstract class AbstractTimeout implements Timeout, Runnable {

    protected final Timer timer;
    protected final TimerTask task;
    protected final long deadline;

    private volatile int state = ST_INIT;
    private static final AtomicIntegerFieldUpdater<AbstractTimeout> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractTimeout.class, "state");


    protected AbstractTimeout(Timer timer, TimerTask task, long deadline) {
        this.timer = timer;
        this.task = task;
        this.deadline = deadline;
        System.out.println("执行事件：" + TimeUnit.MILLISECONDS.convert(this.deadline - System.nanoTime(), TimeUnit.NANOSECONDS));
    }


    public boolean compareAndSetState(int expected, int state) {
        return STATE_UPDATER.compareAndSet(this, expected, state);
    }


    public int state() {
        return state;
    }


    @Override
    public boolean isCancelled() {
        return state() == ST_CANCELLED;
    }


    public void expire() {
        // wheel timeout 的做法是，在expire时 开始执行任务
        if (!compareAndSetState(ST_INIT, ST_EXPIRED)) {
            System.out.println("状态不对");
            return;
        }
        try {
            System.out.println("executeTask..." + System.currentTimeMillis());
            executeTask();
        } catch (Throwable t) {
            Logger logger = Loggers.getLogger(HashedWheelTimeout.class);
            if (logger.isWarnEnabled()) {
                logger.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
            }
        }
    }


    /**
     * @return 返回true时，说明 该任务正在被执行，或者已执行完毕。 未被执行时，返回false
     */
    @Override
    public boolean isExpired() {
        return state() == ST_EXPIRED;
    }


    /**
     * * <p>
     * executeTask() -> timer.getTaskExecutor().execute(this) => 在 executor中执行 timeout.run()方法 => 在executor 中执行 TimerTask
     */
    public void executeTask() {
        Executor executor = timer.getTaskExecutor();
        if (executor == null) {
            executor = ImmediateExecutor.INSTANCE;
        }
        executor.execute(this);
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            task.run(this);
        } catch (Throwable t) {
            Logger logger = Loggers.getLogger(getClass());
            if (logger.isWarnEnabled()) {
                logger.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
            }
            throw new Error(t);
        }
    }


    @Override
    public Timer timer() {
        return timer;
    }

    @Override
    public TimerTask task() {
        return task;
    }


}
