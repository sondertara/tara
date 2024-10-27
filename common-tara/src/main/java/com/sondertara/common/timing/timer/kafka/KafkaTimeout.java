package com.sondertara.common.timing.timer.kafka;

import com.sondertara.common.timing.timer.RunnableToTimerTaskAdapter;
import com.sondertara.common.timing.timer.Timeout;
import com.sondertara.common.timing.timer.Timer;

import java.util.Objects;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/8 18:21
 */
public class KafkaTimeout extends TimerTask implements Timeout {
    final Timer timer;
    final com.sondertara.common.timing.timer.TimerTask timerTask;

    public KafkaTimeout(Timer timer, Runnable runnable, long expirationMs) {
        this(timer, new RunnableToTimerTaskAdapter(runnable), expirationMs);

    }

    public KafkaTimeout(Timer timer, com.sondertara.common.timing.timer.TimerTask timerTask, long expirationMs) {
        super(expirationMs);
        this.timerTask = timerTask;


        this.timer = timer;
    }

    @Override
    public void run() {
        try {
            timerTask.run(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Timer timer() {
        return timer;
    }

    @Override
    public com.sondertara.common.timing.timer.TimerTask task() {
        return this.timerTask;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() <= expirationMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KafkaTimeout that = (KafkaTimeout) o;
        return Objects.equals(timerTask, that.timerTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timerTask);
    }
}
