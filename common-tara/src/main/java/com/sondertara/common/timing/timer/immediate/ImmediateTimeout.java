package com.sondertara.common.timing.timer.immediate;

import com.sondertara.common.timing.timer.AbstractTimeout;
import com.sondertara.common.timing.timer.Timer;
import com.sondertara.common.timing.timer.TimerTask;

/**
 *  */
public class ImmediateTimeout extends AbstractTimeout {
    @Override
    public boolean cancel() {
        return false;
    }

    public ImmediateTimeout(Timer timer, TimerTask task, long deadline) {
        super(timer, task, deadline);
    }

    @Override
    public void executeTask() {
        if (!compareAndSetState(ST_INIT, ST_EXPIRED)) {
            return;
        }
        super.executeTask();
    }
}
