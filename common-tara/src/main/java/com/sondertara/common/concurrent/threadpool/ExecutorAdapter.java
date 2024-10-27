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

import com.sondertara.common.base.Named;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorAdapter inherits Executor, the goal of this interface is to be
 * as compatible as possible with {@link java.util.concurrent.ThreadPoolExecutor}.
 *
 * @author dragon-zhang
 * * @param <E> the executor type
 **/
public interface ExecutorAdapter extends Executor, Named {


    /**
     * Get the core pool size
     *
     * @return the core pool size
     */
    int getCorePoolSize();

    /**
     * Set the core pool size
     *
     * @param corePoolSize the core pool size
     */
    void setCorePoolSize(int corePoolSize);

    /**
     * Get the maximum pool size
     *
     * @return the maximum pool size
     */
    int getMaximumPoolSize();

    default public boolean prestartCoreThread() {
        //no-op
        return false;
    }

    default public int prestartAllCoreThreads() {
        //no-op
        return -1;
    }

    default public boolean remove(Runnable runnable) {
        //no-op
        return false;
    }

    /**
     * Tries to remove from the work queue all {@link Future}
     * tasks that have been cancelled. This method can be useful as a
     * storage reclamation operation, that has no other impact on
     * functionality. Cancelled tasks are never executed, but may
     * accumulate in work queues until worker threads can actively
     * remove them. Invoking this method instead tries to remove them now.
     * However, this method may fail to remove tasks in
     * the presence of interference by other threads.
     */
    default public void purge() {
        //no-op
    }


    /**
     * Set the maximum pool size
     *
     * @param maximumPoolSize the maximum pool size
     */
    void setMaximumPoolSize(int maximumPoolSize);

    /**
     * Get the pool size
     *
     * @return the pool size
     */
    int getPoolSize();

    /**
     * Get the active count
     *
     * @return the active count
     */
    int getActiveCount();

    /**
     * Get the largest pool size
     *
     * @return the largest pool size
     */
    default int getLargestPoolSize() {
        //default unsupported
        return -1;
    }

    /**
     * Get the task count
     *
     * @return the task count
     */
    default long getTaskCount() {
        //default unsupported
        return -1;
    }

    /**
     * Get the completed task count
     *
     * @return the completed task count
     */
    default long getCompletedTaskCount() {
        //default unsupported
        return -1;
    }

    public void setThreadFactory(ThreadFactory threadFactory);

    /**
     * Get the queue
     *
     * @return the queue
     */
    default BlockingQueue<Runnable> getQueue() {
        return new UnsupportedBlockingQueue();
    }

    /**
     * Get the queue type
     *
     * @return the queue type
     */
    default String getQueueType() {
        return getQueue().getClass().getSimpleName();
    }

    /**
     * Get the queue size
     *
     * @return the queue size
     */
    default int getQueueSize() {
        return getQueue().size();
    }

    /**
     * Get the queue remaining capacity
     *
     * @return the queue remaining capacity
     */
    default int getQueueRemainingCapacity() {
        return getQueue().remainingCapacity();
    }

    /**
     * Get the queue capacity
     *
     * @return the queue capacity
     */
    default int getQueueCapacity() {
        int capacity = getQueueSize() + getQueueRemainingCapacity();
        return capacity < 0 ? Integer.MAX_VALUE : capacity;
    }

    /**
     * On refresh queue capacity.
     *
     * @param capacity the queue capacity
     */
    default void onRefreshQueueCapacity(int capacity) {
        //default do nothing
    }

    /**
     * Get the rejected execution handler
     *
     * @return the rejected execution handler
     */
    default RejectedExecutionHandler getRejectedExecutionHandler() {
        //default unsupported
        return null;
    }

    /**
     * Set the rejected execution handler
     *
     * @param handler the rejected execution handler
     */
    default void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        //default unsupported
    }

    /**
     * Get the reject handler type
     *
     * @return the reject handler type
     */
    default String getRejectHandlerType() {
        return Optional.ofNullable(getRejectedExecutionHandler())
                .map(h -> h.getClass().getSimpleName())
                .orElse("unknown");
    }

    /**
     * If allow core thread time out
     *
     * @return if allow core thread time out
     */
    default boolean allowsCoreThreadTimeOut() {
        //default unsupported
        return false;
    }

    /**
     * Allow core thread time out
     *
     * @param value if allow core thread time out
     */
    default void allowCoreThreadTimeOut(boolean value) {
        //default unsupported
    }

    /**
     * Get the keep alive time
     *
     * @param unit the time unit
     * @return the keep alive time
     */
    default long getKeepAliveTime(TimeUnit unit) {
        //default unsupported
        return -1;
    }

    /**
     * Set the keep alive time
     *
     * @param time the keep alive time
     * @param unit the time unit
     */
    default void setKeepAliveTime(long time, TimeUnit unit) {
        //default unsupported
    }


    /**
     * is terminating
     *
     * @return boolean
     */
    default boolean isTerminating() {
        //default unsupported
        return false;
    }

    ThreadFactory getThreadFactory();

    class UnsupportedBlockingQueue extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {

        @Override
        public Iterator<Runnable> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void put(Runnable runnable) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean offer(Runnable runnable, long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable take() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable poll(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int remainingCapacity() {
            return 0;
        }

        @Override
        public int drainTo(Collection<? super Runnable> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int drainTo(Collection<? super Runnable> c, int maxElements) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean offer(Runnable runnable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable poll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable peek() {
            throw new UnsupportedOperationException();
        }
    }
}
