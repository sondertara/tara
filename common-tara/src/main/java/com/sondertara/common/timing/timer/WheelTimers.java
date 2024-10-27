package com.sondertara.common.timing.timer;

import com.sondertara.common.concurrent.DefaultThreadFactory;
import com.sondertara.common.timing.timer.kafka.KafkaTimer;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WheelTimers {
    private WheelTimers() {

    }

    public static HashedWheelTimer newHashedWheelTimer() {
        return newHashedWheelTimer(null);
    }

    public static HashedWheelTimer newHashedWheelTimer(Executor executor) {
        return newHashedWheelTimer(executor, 100, TimeUnit.MILLISECONDS);
    }

    public static KafkaTimer newKafkaTimer(Executor executor) {
        return new KafkaTimer(executor);
    }

    public static KafkaTimer newKafkaTimer(Executor executor, long tickDuration, TimeUnit unit) {
        return new KafkaTimer(executor,unit.toMillis(tickDuration));
    }



    public static HashedWheelTimer newHashedWheelTimer(Executor executor, long tickDuration, TimeUnit unit) {
        return newHashedWheelTimer(executor, tickDuration, unit, 512);
    }

    public static HashedWheelTimer newHashedWheelTimer(Executor executor, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        return newHashedWheelTimer(executor, null, tickDuration, unit, ticksPerWheel, false);
    }


    public static HashedWheelTimer newHashedWheelTimer(Executor executor, ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel, boolean leakDetection) {
        return new HashedWheelTimer(threadFactory == null ? new DefaultThreadFactory("Hashed-Timer", true) : threadFactory, tickDuration, unit, ticksPerWheel, leakDetection, -1, executor);
    }
}
