package com.sondertara.common.timing.timer;

public class RunnableToTimerTaskAdapter implements TimerTask {
    protected Runnable runnable;

    public RunnableToTimerTaskAdapter(Runnable r) {
        this.runnable = r;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (!timeout.isCancelled()) {
            runnable.run();
        }
    }
}
