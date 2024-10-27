package com.sondertara.common.timing.timer;

/**
 *
 * @author huangxiaohu.1ih*/
public class DistinctHashedWheelTimeoutFactory implements TimeoutFactory<HashedWheelTimer, DistinctHashedWheelTimeout> {
    public static final DistinctHashedWheelTimeoutFactory INSTANCE = new DistinctHashedWheelTimeoutFactory();

    @Override
    public DistinctHashedWheelTimeout create(HashedWheelTimer timer, TimerTask task, long deadline) {
        return new DistinctHashedWheelTimeout( timer, task, deadline);
    }
}
