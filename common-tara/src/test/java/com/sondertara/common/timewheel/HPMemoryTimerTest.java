package com.sondertara.common.timewheel;

import com.sondertara.common.concurrent.threadpool.ThreadPoolFactory;
import com.sondertara.common.random.RandomUtils;
import com.sondertara.common.timing.scheduling.PeriodicTrigger;
import com.sondertara.common.timing.timer.HashedWheelTimer;
import com.sondertara.common.timing.timer.ReschedulingTask;
import com.sondertara.common.timing.timer.Timeout;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HPMemoryTimerTest {
    @Test
    public void test() throws InterruptedException {
        ExecutorService executor = ThreadPoolFactory.getInstance().newFixedThreadPool("rrrr", 16);
        HashedWheelTimer timer = new HashedWheelTimer(250,TimeUnit.MILLISECONDS,1024,executor);

        AtomicInteger count = new AtomicInteger(0);

        ReschedulingTask reschedulingTask = new ReschedulingTask(timer, new Runnable() {
            @Override
            public void run() {
                System.out.println("急急急0:" + System.currentTimeMillis());
                try {
                    Thread.sleep(1000 + RandomUtils.randomInt(10) * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("execution time:" + count.get());
                count.getAndIncrement();
                System.out.println("急急急1:" + System.currentTimeMillis());


            }
        }, new PeriodicTrigger(2, TimeUnit.SECONDS), e -> {
            System.out.println(e.getMessage());
        });
        Timeout timeout = reschedulingTask.schedule();


        while (true) {
            if (count.get() > 5) {
                boolean cancel = timeout.cancel();
                break;
            }
        }
        System.out.println("ffff");
        TimeUnit.HOURS.sleep(1);
    }
}
