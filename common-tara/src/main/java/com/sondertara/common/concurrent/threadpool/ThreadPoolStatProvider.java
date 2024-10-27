/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sondertara.common.concurrent.threadpool;

import com.sondertara.common.concurrent.runnable.TimeoutRunnable;
import com.sondertara.common.concurrent.threadpool.aware.ThreadTimeoutTask;
import com.sondertara.common.timing.timer.Timeout;
import com.sondertara.common.timing.timer.Timer;
import com.sondertara.common.timing.timer.WheelTimers;
import lombok.Getter;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * Stat provider for thread pool.
 *
 * @author hanli
 */
public class ThreadPoolStatProvider {

    private static final MetricThreadPoolExecutor METRIC_EXECUTOR = ThreadPoolFactory.getInstance()
            .createPool(ThreadPoolConfigure.builder()
                    .key("Thread-Metric").daemon(true)
                    .allowCoreThreadTimeOut(true)
                    .corePoolSize(8)
                    .maxPoolSize(16).queue(2048).build());

    private static Timer TIMER = null;


    static {
        try {
            TIMER = WheelTimers.newHashedWheelTimer(METRIC_EXECUTOR, 1000, TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {
            //no-op
        }
    }

    private final MetricThreadPoolExecutor executor;

    /**
     * Task execute timeout, unit (ms), just for statistics.
     */
    @Getter
    private long runTimeoutMs = 0;

    /**
     * Task queue wait timeout, unit (ms), just for statistics.
     */
    @Getter
    private long queueTimeoutMs = 0;

    /**
     * Total reject count.
     */
    private final LongAdder rejectCount = new LongAdder();

    /**
     * Count run timeout tasks.
     */
    private final LongAdder runTimeoutCount = new LongAdder();
    /**
     * Count task is running
     */
    private final LongAdder runningCount = new LongAdder();

    /**
     * Count queue wait timeout tasks.
     */
    private final LongAdder completeTaskCount = new LongAdder();

    private final LongAdder runErrorTaskCount = new LongAdder();

    /**
     * runTimeoutMap  key -> Runnable  value -> Timeout
     */
    private final Map<Runnable, SoftReference<Timeout>> runTimeoutMap = new ConcurrentHashMap<>();

    /**
     * queueTimeoutMap  key -> Runnable  value -> Timeout
     */
    private final Map<Runnable, SoftReference<Timeout>> queueTimeoutMap = new ConcurrentHashMap<>();

    public ThreadPoolStatProvider(MetricThreadPoolExecutor executor) {
        this.executor = executor;
        this.runTimeoutMs = executor.getThreadTimeout();
    }


    public void setRunTimeout(long runTimeout) {
        this.runTimeoutMs = runTimeout;
    }

    public void setQueueTimeout(long queueTimeout) {
        this.queueTimeoutMs = queueTimeout;
    }

    public long getRejectedTaskCount() {
        return rejectCount.sum();
    }

    public void incRejectCount(int count) {
        rejectCount.add(count);
    }

    public void incCompleteTaskCount(int count) {
        completeTaskCount.add(count);
    }

    public void incRunErrorTaskCount(int count) {
        runErrorTaskCount.add(count);
    }


    public int getRunningCount() {
        return (int) runningCount.sum();
    }

    public void incRunningCount() {
        runningCount.increment();
    }

    public void decRunningCount() {
        runningCount.decrement();

    }


    public long getRunTimeoutCount() {
        return runTimeoutCount.sum();
    }

    public long getRunErrorTaskCount() {
        return runErrorTaskCount.sum();
    }

    public long getCompleteTaskCount() {
        return completeTaskCount.sum();
    }


    public void incRunTimeoutCount(int count) {
        runTimeoutCount.add(count);
    }


    public void startRunTimeoutTask(Thread t, Runnable r) {
        if (runTimeoutMs <= 0) {
            return;
        }

        long runnableTimeout = runTimeoutMs;
        if (r instanceof TimeoutRunnable) {
            long threadTimeout = ((TimeoutRunnable) r).runTimeout();
            if (threadTimeout > 0) {
                runnableTimeout = threadTimeout;
            }
        }
        if (null != TIMER) {
            Timeout timeout = TIMER.newTimeout(new ThreadTimeoutTask(this, r), runnableTimeout, TimeUnit.SECONDS);
            runTimeoutMap.put(r, new SoftReference<>(timeout));
        }
    }

    public void cancelRunTimeoutTask(Runnable r) {
        Optional.ofNullable(runTimeoutMap.remove(r)).map(SoftReference::get).ifPresent(Timeout::cancel);
    }

    public long getTaskCount() {
        return getRunningCount() + getRejectedTaskCount() + executor.getQueueSize() + getRunErrorTaskCount() + getCompleteTaskCount();
    }
}
