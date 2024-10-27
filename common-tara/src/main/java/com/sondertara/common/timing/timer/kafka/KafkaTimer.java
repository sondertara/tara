/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sondertara.common.timing.timer.kafka;

import com.sondertara.common.concurrent.ShutdownableThread;
import com.sondertara.common.concurrent.ThreadUtils;
import com.sondertara.common.timing.timer.AbstractTimer;
import com.sondertara.common.timing.timer.Timeout;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class KafkaTimer extends AbstractTimer {
    public static final String SYSTEM_TIMER_THREAD_PREFIX = "executor-";

    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    private final CountDownLatch startTimeInitialized = new CountDownLatch(1);
    private static final AtomicIntegerFieldUpdater<KafkaTimer> WORKER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(KafkaTimer.class, "workerState");
    /**
     * 0 - init, 1 - started, 2 - shut down
     */
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    private volatile int workerState;
    private final DelayQueue<TimerTaskList> delayQueue;
    private final AtomicInteger taskCounter;
    private final TimingWheel timingWheel;

    // Locks used to protect data structures while ticking
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    private volatile boolean ticking = false;
    private volatile long startTime;
    private ShutdownableThread tickThread = null;

    public KafkaTimer(Executor executorService) {
        this(10, 512, System.currentTimeMillis(), executorService);
    }

    public KafkaTimer(Executor executorService,long tickMs) {
        this(tickMs, 512, System.currentTimeMillis(), executorService);
    }

    public KafkaTimer(
            long tickMs,
            int wheelSize,
            long startMs, Executor executor
    ) {
        setTaskExecutor(executor);
        this.delayQueue = new DelayQueue<>();
        this.taskCounter = new AtomicInteger(0);
        this.timingWheel = new TimingWheel(
                tickMs,
                wheelSize,
                startMs,
                taskCounter,
                delayQueue
        );
    }

    /**
     * Starts the background thread explicitly.  The background thread will
     * start automatically on demand even if you did not call this method.
     *
     * @throws IllegalStateException if this timer has been
     *                               {@linkplain #stop() stopped} already
     */
    public void start() {
        switch (WORKER_STATE_UPDATER.get(this)) {
            case WORKER_STATE_INIT:
                if (WORKER_STATE_UPDATER.compareAndSet(this, WORKER_STATE_INIT, WORKER_STATE_STARTED)) {
                    // Initialize the startTime.
                    startTime = System.nanoTime();
                    if (startTime == 0) {
                        // We use 0 as an indicator for the uninitialized value here, so make sure it's not 0 when initialized.
                        startTime = 1;
                    }
                    this.tickThread = new ShutdownableThread("KafkaTimer-Tick") {
                        @Override
                        public void doWork() {
                            try {
                                advanceClock(20);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    };
                    this.tickThread.start();
                    // Notify the other threads waiting for the initialization at start().
                    startTimeInitialized.countDown();
                }
                this.running = true;
                break;
            case WORKER_STATE_STARTED:
                this.running = true;
                break;
            case WORKER_STATE_SHUTDOWN:
                this.running = false;
                throw new IllegalStateException("cannot be started once stopped");
            default:
                throw new Error("Invalid WorkerState");
        }

        // Wait until the startTime is initialized by the worker.
        while (startTime == 0) {
            try {
                startTimeInitialized.await();
            } catch (InterruptedException ignore) {
                boolean interrupted = Thread.interrupted();
                if (interrupted) {
                    // Ignore - it will be ready very soon.
                }
            }
        }
    }

    void add(TimerTask timerTask) {
        start();
        readLock.lock();
        try {
            addTimerTaskEntry(new TimerTaskEntry(timerTask, timerTask.expirationMs));
        } finally {
            readLock.unlock();
        }
    }

    private void addTimerTaskEntry(TimerTaskEntry timerTaskEntry) {
        if (!timingWheel.add(timerTaskEntry)) {
            // Already expired or cancelled
            if (!timerTaskEntry.cancelled()) {
                getTaskExecutor().execute(timerTaskEntry.timerTask);
            }
        }
    }

    /**
     * Advance the internal clock, executing any tasks whose expiration has been
     * reached within the duration of the passed timeout.
     * <p>
     * <p>
     * /**
     * Advances the clock if there is an expired bucket. If there isn't any expired bucket when called,
     * waits up to timeoutMs before giving up.
     *
     * @param timeoutMs the time to advance in milliseconds
     * @return whether or not any tasks were executed
     */
    boolean advanceClock(long timeoutMs) throws InterruptedException {
        TimerTaskList bucket = delayQueue.poll(timeoutMs, TimeUnit.MILLISECONDS);
        if (bucket != null) {
            writeLock.lock();
            try {
                while (bucket != null) {
                    timingWheel.advanceClock(bucket.getExpiration());
                    bucket.flush(this::addTimerTaskEntry);
                    bucket = delayQueue.poll();
                }
            } finally {
                writeLock.unlock();
            }
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        return taskCounter.get();
    }


    @Override
    public Timeout newTimeout(Runnable task, long delay, TimeUnit unit) {
        KafkaTimeout timeout = new KafkaTimeout(this, task, System.currentTimeMillis() + unit.toMillis(delay));
        add(timeout);
        return timeout;


    }

    @Override
    public Timeout newTimeout(com.sondertara.common.timing.timer.TimerTask task, long delay, TimeUnit unit) {
        KafkaTimeout timeout = new KafkaTimeout(this, task, System.currentTimeMillis() + unit.toMillis(delay));
        add(timeout);
        return timeout;
    }

    @Override
    public Set<Timeout> stop() {
        if (null != tickThread) {
            try {
                tickThread.shutdown();
            } catch (Exception e) {
                log.error("Kafka timer shutdown ticker error,{}", e.getMessage(), e);
            }
        }
        if (taskExecutor instanceof ExecutorService) {
            ThreadUtils.shutdownThreadPool((ExecutorService) taskExecutor, 5);
        }

        Set<Timeout> set = new LinkedHashSet<>();
        ArrayList<TimerTaskList> objects = new ArrayList<>();
        delayQueue.drainTo(objects);
        objects.forEach(s -> {
            s.foreach(t -> {
                KafkaTimeout kafkaTimeout = (KafkaTimeout) t;
                set.add(kafkaTimeout);
            });

        });

        return set;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.nanoTime() / 1e6);
        System.out.println(System.currentTimeMillis());
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        KafkaTimer kafkaTimer = new KafkaTimer(10, 512, System.currentTimeMillis(), executorService);

        AtomicInteger atomicInteger = new AtomicInteger(0);
        Runnable runnable = () -> {
            System.out.println(System.currentTimeMillis());
            System.out.println("测试:" + atomicInteger.incrementAndGet());
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        kafkaTimer.newTimeout(runnable, 10, TimeUnit.SECONDS);
        kafkaTimer.newTimeout(runnable, 20, TimeUnit.SECONDS);

        Thread.sleep(TimeUnit.SECONDS.toMillis(30));
        Set<Timeout> stop = kafkaTimer.stop();
        System.out.println(stop.size());
    }
}
