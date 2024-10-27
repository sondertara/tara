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

package com.sondertara.common.concurrent;


import com.sondertara.common.lifecycle.Lifecycle;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class ShutdownableThread extends Thread implements Lifecycle {

    public final String logPrefix;


    private final boolean isInterruptible;

    private final CountDownLatch shutdownInitiated = new CountDownLatch(1);
    private final CountDownLatch shutdownComplete = new CountDownLatch(1);

    private volatile boolean isStarted = false;

    public ShutdownableThread(String name) {
        this(name, true);
    }

    public ShutdownableThread(String name, boolean isInterruptible) {
        this(name, isInterruptible, "[" + name + "]: ");
    }

    @SuppressWarnings("this-escape")
    public ShutdownableThread(String name, boolean isInterruptible, String logPrefix) {
        super(name);
        this.isInterruptible = isInterruptible;
        this.logPrefix = logPrefix;
        this.setDaemon(false);
    }

    @Override
    public void shutdown() {
        initiateShutdown();
        awaitShutdown();
    }

    public boolean isShutdownInitiated() {
        return shutdownInitiated.getCount() == 0;
    }

    public boolean isShutdownComplete() {
        return shutdownComplete.getCount() == 0;
    }

    public boolean isStarted() {
        return isStarted;
    }

    /**
     * @return true if there has been an unexpected error and the thread shut down
     */
    // mind that run() might set both when we're shutting down the broker
    // but the return value of this function at that point wouldn't matter
    public boolean isThreadFailed() {
        return isShutdownComplete() && !isShutdownInitiated();
    }

    /**
     * @return true if the thread hasn't initiated shutdown already
     */
    public boolean initiateShutdown() {
        synchronized (this) {
            if (isRunning()) {
                log.info("Shutting down");
                shutdownInitiated.countDown();
                if (isInterruptible)
                    interrupt();
                return true;
            } else
                return false;
        }
    }

    /**
     * After calling initiateShutdown(), use this API to wait until the shutdown is complete.
     */
    public void awaitShutdown() {
        if (!isShutdownInitiated())
            throw new IllegalStateException("initiateShutdown() was not called before awaitShutdown()");
        else {
            if (isStarted) {
                try {
                    shutdownComplete.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("Thread[{}] shutdown completed", getName());
        }
    }

    /**
     * Causes the current thread to wait until the shutdown is initiated,
     * or the specified waiting time elapses.
     *
     * @param timeout wait time in units.
     * @param unit    TimeUnit value for the wait time.
     */
    public void pause(long timeout, TimeUnit unit) throws InterruptedException {
        if (shutdownInitiated.await(timeout, unit))
            log.trace("shutdownInitiated latch count reached zero. Shutdown called.");
    }

    /**
     * This method is repeatedly invoked until the thread shuts down or this method throws an exception
     */
    public abstract void doWork();

    public void run() {
        isStarted = true;
        log.info("Starting");
        try {
            while (isRunning())
                doWork();
        } catch (Exception e) {
            shutdownInitiated.countDown();
            shutdownComplete.countDown();
            log.info("Stopped");
        } catch (Throwable e) {
            if (isRunning()) {
                log.error("Error due to", e);
            }
        } finally {
            shutdownComplete.countDown();
        }
        log.info("Stopped");
    }

    public boolean isRunning() {
        return !isShutdownInitiated();
    }

    @Override
    public void startup() {
        start();
    }
}
