package com.sondertara.common.timing.timer;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 *  */
public class DistinctHashedWheelTimeout extends HashedWheelTimeout {

    private static final ConcurrentHashMap<HashedWheelTimer, ConcurrentHashMap<DistinctHashedWheelTimeout, Integer>> RUNNING_TASKS = new ConcurrentHashMap<HashedWheelTimer, ConcurrentHashMap<DistinctHashedWheelTimeout, Integer>>();

    public DistinctHashedWheelTimeout(HashedWheelTimer timer, TimerTask task, long deadline) {
        super(timer, task, deadline);
    }

    @Override
    public void executeTask() {
        HashedWheelTimer hashedWheelTimer = (HashedWheelTimer) timer;
        ConcurrentHashMap<DistinctHashedWheelTimeout, Integer> currentTimerRunningTasks = RUNNING_TASKS.get(hashedWheelTimer);
        if (currentTimerRunningTasks == null) {
            RUNNING_TASKS.putIfAbsent(hashedWheelTimer, new ConcurrentHashMap<DistinctHashedWheelTimeout, Integer>());
            currentTimerRunningTasks = RUNNING_TASKS.get(hashedWheelTimer);
        }
        // 如果 taskExecutor 是ThreadPool时，这个代码可以避免加入到 queue里
        if (!currentTimerRunningTasks.containsKey(this)) {
            // 这里不能将该timeout 放到currentTimerRunningTasks
            super.executeTask();
        }
    }

    /**
     *      */
    @Override
    public void run() {
        try {
            HashedWheelTimer hashedWheelTimer = (HashedWheelTimer) timer;
            ConcurrentHashMap<DistinctHashedWheelTimeout, Integer> currentTimerRunningTasks = RUNNING_TASKS.get(hashedWheelTimer);
            if (currentTimerRunningTasks == null) {
                RUNNING_TASKS.putIfAbsent(hashedWheelTimer, new ConcurrentHashMap<DistinctHashedWheelTimeout, Integer>());
                currentTimerRunningTasks = RUNNING_TASKS.get(hashedWheelTimer);
            }
            if (!currentTimerRunningTasks.containsKey(this)) {
                currentTimerRunningTasks.put(this, 1);
                task.run(this);
                currentTimerRunningTasks.remove(this);
            }
        } catch (Throwable t) {
            Logger logger = Loggers.getLogger(HashedWheelTimer.class);
            if (logger.isWarnEnabled()) {
                logger.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
            }
        }
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof DistinctHashedWheelTimeout)) {
            return false;
        }
        DistinctHashedWheelTimeout that = (DistinctHashedWheelTimeout) obj;
        if(this==that){
            return true;
        }
        if (that.timer != this.timer) {
            return false;
        }
        return ObjectUtils.equals(this.task, that.task);
    }

    @Override
    public final int hashCode() {
        return this.task.hashCode();
    }
}
