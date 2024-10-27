package com.sondertara.common.timing.timer;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.concurrent.ImmediateExecutor;

import java.util.concurrent.Executor;

/**
 * @author huangxiaohu.1ih
 */
public abstract class AbstractTimer implements Timer {
    protected Executor taskExecutor;
    protected volatile boolean running;

    protected void setTaskExecutor(Executor e){
        this.taskExecutor = ObjectUtils.defaultIfEmpty(e, ImmediateExecutor.INSTANCE);
    }

    @Override
    public Executor getTaskExecutor() {
        return taskExecutor;
    }

    @Override
    public boolean isDistinctSupported() {
        return false;
    }

    /**
     *      */
    @Override
    public boolean isRunning() {
        return running;
    }
}
