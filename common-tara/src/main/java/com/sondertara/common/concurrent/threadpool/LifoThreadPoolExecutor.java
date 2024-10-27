/*
 * Copyright (c) 2001-2017, Zoltan Farkas All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Additionally licensed with:
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sondertara.common.concurrent.threadpool;

import com.sondertara.common.annotation.GuardedBy;
import com.sondertara.common.collection.SimpleStack;
import com.sondertara.common.concurrent.threadpool.policy.AbortPolicyWithReport;
import com.sondertara.common.random.RandomUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * LIFO scheduled java thread pool, this behavior is identical  with the JDK's fork join pool, but different from the
 * older jdk ThreadPoolExecutor implementations.
 * <p>
 * This implementation behaves differently compared with a java Thread pool in that it prefers to spawn a
 * thread if possible instead of queueing a task when nr of threads has room to grow.
 * <p>
 * Performance of this pool implementation is similar to ThreadPoolExecutor however due to LIFO scheduling,
 * it will have lower resource usage in most use cases.
 * <p>
 * The JDK Fork Join pool's performance is superior to this implementation, however unlike this implementation,
 * with the fork join pool you will not be able to cancel+interrupt running tasks.
 *
 * @author zoly
 */
public final class LifoThreadPoolExecutor extends AbstractExecutorService implements LifoThreadPool {
    private static final RejectedExecutionHandler DEFAULT_HANDLER = new AbortPolicyWithReport();

    private static final Runnable NOP = () -> {
    };


    private static final Logger LOG = LoggerFactory.getLogger(LifoThreadPoolExecutor.class);
    /**
     * when a thread survives due core size, this the minimum wait time that core threads will wait for. worker threads
     * have a maximum time they are idle, after which they are retired... in case a user configures a thread pool with
     * idle times less than min wait, the core threads will have to have a minimum wait time to avoid spinning and hogging
     * the CPU. this value is used only when the max idle time of the pool is smaller, and it interferes with thread
     * retirement in that case... I do not see that case as a useful pooling case to be worth trying to optimize it...
     */
    private static final long CORE_MINWAIT_NANOS = Long.getLong("tara.lifoTp.coreMaxWaitNanos", 1000000000);

    private static final int LL_THRESHOLD = Integer.getInteger("tara.lifoTp.llQueueSizeThreshold", 64000);

    private final ReentrantReadWriteLock lock;

    private final Condition stateCondition;

    private volatile RejectedExecutionHandler rejectionHandler;

    private final MutableThreadPoolAdapter mutableThreadPoolAdapter;

    private volatile ThreadFactory threadFactory;

    @GuardedBy("stateLock")
    private final ArrayDeque<Runnable> taskQueue;

    @GuardedBy("stateLock")
    private final SimpleStack<QueuedWorker> threadQueue;


    @GuardedBy("stateLock")
    private int maxThreadCount;

    @GuardedBy("stateLock")
    private final PoolState state;

    @GuardedBy("stateLock")
    private int queueSizeLimit;
    private String name;


    // Public constructors and methods

    public LifoThreadPoolExecutor(String name, int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  int queueSizeLimit) {
        this(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, queueSizeLimit,
                Executors.defaultThreadFactory(), DEFAULT_HANDLER);
    }

    public LifoThreadPoolExecutor(String name, int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  int queueSizeLimit,
                                  ThreadFactory threadFactory) {
        this(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, queueSizeLimit,
                threadFactory, DEFAULT_HANDLER);
    }

    public LifoThreadPoolExecutor(String name, int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  int queueSizeLimit,
                                  RejectedExecutionHandler handler) {
        this(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, queueSizeLimit,
                Executors.defaultThreadFactory(), handler);
    }

    public LifoThreadPoolExecutor(String name, int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  int queueSizeLimit,
                                  ThreadFactory threadFactory,
                                  RejectedExecutionHandler handler) {
        if (corePoolSize > maximumPoolSize) {
            throw new IllegalArgumentException("Core size must be smaller than max size " + corePoolSize
                    + " < " + maximumPoolSize);
        }
        if (corePoolSize < 0 || maximumPoolSize < 0 || keepAliveTime < 0 || queueSizeLimit < 0) {
            throw new IllegalArgumentException("All numberic TP configs must be positive values: "
                    + corePoolSize + ", " + maximumPoolSize + ", " + keepAliveTime
                    + ", " + queueSizeLimit);
        }
        this.name = name;
        this.lock = new ReentrantReadWriteLock();
        this.rejectionHandler = handler;
        this.taskQueue = new ArrayDeque<>(Math.min(queueSizeLimit, LL_THRESHOLD));
        this.queueSizeLimit = queueSizeLimit;
        this.threadFactory = threadFactory;
        this.threadQueue = new SimpleStack<>(Math.min(1024, maximumPoolSize));
        state = new PoolState(corePoolSize, new HashSet<>(Math.min(maximumPoolSize, 2048)));
        state.maxIdleTimeNanos = unit.toNanos(keepAliveTime);
        this.stateCondition = lock.writeLock().newCondition();
//        for (int i = 0; i < corePoolSize; i++) {
//            QueuedWorker worker = new QueuedWorker(threadFactory, threadQueue,
//                    taskQueue, null, state, lock, stateCondition);
//            final Thread t = worker.thread;
//            state.addThread(worker);
//            t.start();
//        }
        //start one worker thread
        prestartCoreThread();
        maxThreadCount = maximumPoolSize;
        this.mutableThreadPoolAdapter = new MutableThreadPoolAdapter(this);
    }


    private void runWorker() {
        QueuedWorker worker = new QueuedWorker(threadFactory, threadQueue,
                taskQueue, null, state, lock, stateCondition);
        final Thread t = worker.thread;
        state.addThread(worker);
        t.start();
    }

    @Override
    public boolean prestartCoreThread() {
        final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            int coreThreads = state.getCoreThreads();
            int currentThreads = state.allThreads.size();
            if (currentThreads < coreThreads) {
                runWorker();
                return true;
            } else {
                return false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int prestartAllCoreThreads() {
        final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            int coreThreads = state.getCoreThreads();
            int currentThreads = state.allThreads.size();
            for (int i = 0; i < coreThreads - currentThreads; i++) {
                runWorker();
            }
            return coreThreads;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getRejectedTaskCount() {
        return this.state.getRejectedTasks();
    }

    @Override
    public long runTimeoutTaskCount() {
        return 0;
    }

    @Override
    public long getRunErrorTaskCount() {
        return 0;
    }

    @Override
    public void execute(final Runnable command) {
        boolean reject = false;
        ReentrantReadWriteLock.WriteLock stateLock = this.lock.writeLock();
        stateLock.lock();
        try {
            if (state.isShutdown()) {
                // if shutting down, reject
                stateLock.unlock();
                state.incrementRejectedTasks();
                this.rejectionHandler.rejectedExecution(command, mutableThreadPoolAdapter);
                return;
            }
            QueuedWorker nqt = threadQueue.pollLast();
            if (nqt != null) {
                nqt.runNext(command);
                stateLock.unlock();
                return;
            }
            int tc = state.getThreadCount();
            // was not able to submit to an existing available thread, will attempt to create a new thread.
            if (tc < maxThreadCount) {
                try {
                    QueuedWorker qt = new QueuedWorker(getThreadFactory(), threadQueue, taskQueue,
                            command, state, lock, stateCondition);
                    final Thread t = qt.thread;
                    state.addThread(qt);
                    t.start();
                } finally {
                    stateLock.unlock();
                }

                return;
            }
            // was not able to submit to an existing available thread, reached the maxThread limit.
            // will attempt to queue the task, and reject if unable to
            reject = taskQueue.size() >= queueSizeLimit || !taskQueue.offer(command);
        } catch (Throwable t) {
            if (stateLock.isHeldByCurrentThread()) {
                stateLock.unlock();
            }
            throw t;
        }
        stateLock.unlock();
        if (reject) {
            state.incrementRejectedTasks();
            rejectionHandler.rejectedExecution(command, mutableThreadPoolAdapter);
        }
    }

    @Override
    public void shutdown() {
        ReentrantReadWriteLock.WriteLock stateLock = this.lock.writeLock();
        stateLock.lock();
        try {
            if (!state.isShutdown()) {
                state.shutdown(); // set the shutdown flag, to reject new submissions.
                QueuedWorker th;
                while ((th = threadQueue.pollLast()) != null) {
                    th.signal(); // signal all waiting threads, so they can start going down.
                }
            }
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public boolean awaitTermination(final long time, final TimeUnit unit) throws InterruptedException {
        long deadlinesNanos = System.nanoTime() + unit.toNanos(time);
        int threadCount;
        ReentrantReadWriteLock.WriteLock stateLock = this.lock.writeLock();
        stateLock.lock();
        try {
            if (!state.isShutdown()) {
                throw new IllegalStateException("Threadpool is not is shutdown mode " + this);
            }
            threadCount = state.getThreadCount();
            long timeoutNs = deadlinesNanos - System.nanoTime();
            while (threadCount > 0) {
                if (timeoutNs > 0) {
                    timeoutNs = stateCondition.awaitNanos(timeoutNs);
                } else {
                    break;
                }
                threadCount = state.getThreadCount();
            }
        } finally {
            stateLock.unlock();
        }
        return threadCount == 0;
    }

    @Override
    @NonNull
    public List<Runnable> shutdownNow() {
        shutdown(); // shutdown
        ReentrantReadWriteLock.WriteLock stateLock = this.lock.writeLock();
        stateLock.lock();
        try {
            int stateState = state.getState();
            if (stateState < 2) {
                state.compareAndSetState(stateState, 2);
            }
            ArrayList<Runnable> list = new ArrayList<>(taskQueue);
            taskQueue.clear();
            return list;
        } finally {
            tryInterruptAll();
            stateLock.unlock();
        }
    }


    private boolean tryInterruptAll() {
        int state1 = state.getState();
        if (state1 == 0 || state1 > 3 || (state1 == 1 && !taskQueue.isEmpty())) {
            return false;
        }
        if (state.compareAndSetState(state1, 3)) {
            for (QueuedWorker worker : state.allThreads) {
                worker.thread.interrupt();
            }
        }
        return state.compareAndSetState(3, 4);
    }

    @Override
    public boolean isShutdown() {
        ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();
        stateLock.lock();
        try {
            return state.isShutdown();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public boolean isTerminated() {
        ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();

        stateLock.lock();
        try {
            return state.isShutdown() && state.getThreadCount() == 0;
        } finally {
            stateLock.unlock();
        }
    }


    @Override
    public int getNrQueuedTasks() {
        ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();

        stateLock.lock();
        try {
            return taskQueue.size();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public int getQueueSizeLimit() {
        ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();

        stateLock.lock();
        try {
            return queueSizeLimit;
        } finally {
            stateLock.unlock();
        }
    }


    @Override
    public void setQueueSizeLimit(final int queueSizeLimit) {
        ReentrantReadWriteLock.WriteLock stateLock = lock.writeLock();
        stateLock.lock();
        try {
            this.queueSizeLimit = queueSizeLimit;
        } finally {
            stateLock.unlock();
        }


    }


    @Override
    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    @Override
    public long getCompletedTaskCount() {
        final ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();
        stateLock.lock();
        try {
            return state.getCompletedTasks();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public int getCorePoolSize() {
        final ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();
        stateLock.lock();
        try {
            return state.getCoreThreads();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectionHandler;
    }

    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (handler == null)
            throw new NullPointerException();
        this.rejectionHandler = handler;
    }


    @Override
    public void setCorePoolSize(int corePoolSize) {
        final ReentrantReadWriteLock.WriteLock stateLock = this.lock.writeLock();
        stateLock.lock();
        try {
            this.state.setCoreThreads(corePoolSize);
        } finally {
            stateLock.unlock();
        }

    }

    //    ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();
    @Override
    public int getMaximumPoolSize() {
        final ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();
        stateLock.lock();
        try {
            return maxThreadCount;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        final ReentrantReadWriteLock.WriteLock stateLock = this.lock.writeLock();

        stateLock.lock();
        try {
            this.maxThreadCount = maximumPoolSize;
        } finally {
            stateLock.unlock();
        }

    }

    @Override
    public int getPoolSize() {
        final ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();

        stateLock.lock();
        try {
            return state.getThreadCount();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public int getActiveCount() {
        final ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();

        stateLock.lock();
        try {
            return state.getActiveCount();
        } finally {
            stateLock.unlock();
        }
    }

    private static final class QueuedWorker implements Runnable {

        private static final AtomicIntegerFieldUpdater<QueuedWorker> WORKER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(QueuedWorker.class, "workerState");
        private final SimpleStack<QueuedWorker> threadQueue;

        private final Queue<Runnable> taskQueue;

        /**
         * Thread this worker is running in.  Null if factory fails.
         */
        final Thread thread;

        @SuppressWarnings({"unused", "FieldMayBeFinal"})
        private volatile int workerState; // 0 - init, 1 - started, 2 -  finish

        @GuardedBy("poolStateLock")
        private final PoolState state;

        private volatile long lastRunNanos;


        private final ReentrantReadWriteLock readWriteLock;
        private final ReentrantReadWriteLock.WriteLock poolStateLock;

        private final Condition poolStateCondition;

        private final Condition submitCondition;

        volatile int completedTasks;

        @Nullable
        volatile private Runnable toRun;

        QueuedWorker(ThreadFactory threadFactory, final SimpleStack<QueuedWorker> threadQueue,
                     final Queue<Runnable> taskQueue,
                     @Nullable final Runnable runFirst, final PoolState state,
                     final ReentrantReadWriteLock readWriteLock, final Condition poolStateCondition) {
            this.threadQueue = threadQueue;
            this.taskQueue = taskQueue;
            this.state = state;
            this.thread = threadFactory.newThread(this);
            this.lastRunNanos = System.nanoTime();
            this.readWriteLock = readWriteLock;
            this.poolStateLock = readWriteLock.writeLock();
            this.submitCondition = this.poolStateLock.newCondition();
            this.poolStateCondition = poolStateCondition;
            this.toRun = runFirst;
            WORKER_STATE_UPDATER.set(this, 0);
        }

        public int getWorkerState() {
            return WORKER_STATE_UPDATER.get(this);
        }

        /**
         * will return false when this thread is not running anymore...
         *
         * @param runnable
         * @return
         */
        private void runNext(final Runnable runnable) {
            toRun = runnable;
            submitCondition.signal();
        }

        private void signal() {
            runNext(NOP);
        }

        @Override
        public void run() {
            Runnable r = toRun;
            if (r != null) {
                try {
                    execute(r);
                } finally {
                    toRun = null;
                }
            }
            doRun();
        }

        private void doRun() {
            try {
                while (true) {
                    final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
                    readLock.lock();
                    Runnable poll = taskQueue.poll();
                    readLock.unlock();
                    if (poll != null) {
                        execute(poll);
                    } else { // nothing in the queue, will put the thread to thread queue.
                        poolStateLock.lock();
                        if (state.isShutdown()) {
                            removeThread();
                            break;
                        }
                        long timeoutNanos = lastRunNanos + state.maxIdleTimeNanos - System.nanoTime();
                        if (timeoutNanos <= 0) { // Thread was idle more than it should
                            final int tc = state.getThreadCount();
                            if (tc > state.getCoreThreads()) { // can we terminate.
                                removeThread();
                                break;
                            } else { // this is a core thread for now.
                                timeoutNanos = CORE_MINWAIT_NANOS;
                            }
                        }
                        int ptr = threadQueue.pushAndGetIdx(this);
                        try {
                            timeoutNanos = submitCondition.awaitNanos(timeoutNanos);
                        } catch (InterruptedException ex) {
                            if (state.isShutdown()) {
                                removeThread();
                                break;
                            }
                        }
                        Runnable r = toRun;
                        if (r != null) {
                            poolStateLock.unlock();
                            try {
                                execute(r);
                            } finally {
                                toRun = null;
                            }
                        } else {
                            QueuedWorker qt = threadQueue.get(ptr);
                            if (qt == this) {
                                threadQueue.remove(ptr);
                            } else {
                                if (!threadQueue.remove(this)) {
                                    throw new IllegalStateException("Thread " + this + " not present in " + threadQueue);
                                }
                            }
                            if (timeoutNanos <= 0) {
                                final int tc = state.getThreadCount();
                                if (state.isShutdown() || tc > state.getCoreThreads()) {
                                    removeThread();
                                    break;
                                }
                            }
                            poolStateLock.unlock();
                        }
                    }
                }
            } catch (Throwable t) {
                LOG.error("Unexpected exception", t);
                if (poolStateLock.isHeldByCurrentThread()) {
                    poolStateLock.unlock();
                }
                throw t;
            }

        }

        private void removeThread() {
            state.removeThread(this);
            poolStateCondition.signalAll();

            poolStateLock.unlock();

        }

        private void execute(final Runnable runnable) {
            Thread wt = Thread.currentThread();
            try {
                if (Thread.interrupted() && !wt.isInterrupted()) {
                    wt.interrupt();
                    return;
                }
                WORKER_STATE_UPDATER.compareAndSet(this, 0, 1);
                try {
                    runnable.run();
                } finally {
//                    completedTasks++;
                    state.incrementCompletedTasks();
                }
            } finally {
                WORKER_STATE_UPDATER.compareAndSet(this, 1, 2);
                lastRunNanos = System.nanoTime();
            }
        }

    }

    private static final class PoolState {
        private static final AtomicIntegerFieldUpdater<PoolState> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(PoolState.class, "poolState");
        @SuppressWarnings({"unused", "FieldMayBeFinal"})
        private volatile int poolState; // 0 - running, 1 - shutdown, 2 - stop, 3 - tidying, 4 - terminated

        private int coreThreads;

        private final Set<QueuedWorker> allThreads;

        private LongAdder rejectedTasks;
        private LongAdder completedTasks;

        volatile long maxIdleTimeNanos;

        PoolState(final int thnr, final Set<QueuedWorker> allThreads) {
            STATE_UPDATER.set(this, 0);
            this.coreThreads = thnr;
            this.allThreads = allThreads;
            this.rejectedTasks = new LongAdder();
            this.completedTasks = new LongAdder();
        }

        public void incrementRejectedTasks() {
            this.rejectedTasks.increment();
        }

        public void incrementCompletedTasks() {
            this.completedTasks.increment();
        }

        public void addThread(final QueuedWorker worker) {
            if (!allThreads.add(worker)) {
                throw new IllegalStateException("Attempting to add a thread twice: " + worker);
            }
            LOG.debug("Started thread {}", worker.thread.getName());
        }

        public boolean compareAndSetState(int expect, int state) {
            return STATE_UPDATER.compareAndSet(this, expect, state);
        }


        public void removeThread(final QueuedWorker worker) {
            if (!allThreads.remove(worker)) {
                throw new IllegalStateException("Removing thread failed: " + worker);
            }

            LOG.debug("Terminating thread {}", worker.thread.getName());
        }


        public int getCoreThreads() {
            return coreThreads;
        }


        public int getState() {
            return STATE_UPDATER.get(this);
        }

        public int getCompletedTasks() {
            return this.completedTasks.intValue();
        }

        public int getRejectedTasks() {
            return this.rejectedTasks.intValue();
        }


        public int getActiveCount() {
            int n = 0;
            for (QueuedWorker thread : allThreads) {
                if (thread.getWorkerState() == 1) {
                    n++;
                }
            }
            return n;
        }

        public void setCoreThreads(final int setCoreThreads) {
            coreThreads = setCoreThreads;
        }

        public boolean isShutdown() {
            return STATE_UPDATER.get(this) != 0;
        }

        public void shutdown() {
            STATE_UPDATER.set(this, 1);
        }

        public int getThreadCount() {
            return allThreads.size();
        }

        @Override
        public String toString() {
            return "ExecState{" + "state=" + getState() + ", threadCount="
                    + allThreads.size() + '}';
        }

    }

    @Override
    public String toString() {

        return "LifoThreadPoolExecutorSQP{" + "threadQueue=" + threadQueue.size() + ", maxIdleTimeMillis="
                + TimeUnit.NANOSECONDS.toMillis(state.maxIdleTimeNanos) + ", maxThreadCount=" + maxThreadCount + ", state=" + state
                + ", submitMonitor=" + lock + ", queueCapacity=" + queueSizeLimit + '}';
    }


    @Override
    public boolean allowsCoreThreadTimeOut() {
        return LifoThreadPool.super.allowsCoreThreadTimeOut();
    }

    @Override
    public void allowCoreThreadTimeOut(boolean value) {
        LifoThreadPool.super.allowCoreThreadTimeOut(value);
    }

    @Override
    public long getKeepAliveTime(TimeUnit unit) {
        ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();
        stateLock.lock();
        try {
            return unit.convert(state.maxIdleTimeNanos, TimeUnit.NANOSECONDS);
        } finally {
            stateLock.unlock();
        }

    }

    @Override
    public void setKeepAliveTime(long time, TimeUnit unit) {
        ReentrantReadWriteLock.WriteLock stateLock = this.lock.writeLock();
        stateLock.lock();
        try {
            state.maxIdleTimeNanos = unit.toNanos(time);
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public boolean isTerminating() {
        ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();
        stateLock.lock();
        try {
            return state.getState() == 2;
        } finally {
            stateLock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        AtomicInteger count = new AtomicInteger(0);

        LifoThreadPoolExecutor executorSQP = new LifoThreadPoolExecutor("TEST", 2, 4, 10, TimeUnit.SECONDS, 20);

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executorService.execute(() -> {
                executorSQP.execute(() -> {

                    try {
                        System.out.println("执行" + finalI);
                        Thread.sleep(RandomUtils.randomInt(0, 30) * 1000L);
                        System.out.println("完毕" + count.incrementAndGet());
                    } catch (InterruptedException e) {
                        System.out.println("停止" + finalI);
                        Thread.currentThread().interrupt();
                    }

                });
            });
        }

        while (count.get() < 9) {
            int activeCount = executorSQP.getActiveCount();
            long completedTaskCount = executorSQP.getCompletedTaskCount();
            int queueSize = executorSQP.getQueueSize();
            long rejectedTaskCount = executorSQP.getRejectedTaskCount();
            System.out.println("activeCount:" + activeCount + " completedTaskCount:" + completedTaskCount + " rejectedTaskCount:" + rejectedTaskCount + " queueSize:" + queueSize);
            Thread.sleep(500);
            if (count.get() >= 3) {
                List<Runnable> list = executorSQP.shutdownNow();
                System.out.println("未执行" + list.size());
                System.out.println("isTerminated:" + executorSQP.isTerminated());
            }
        }
    }

    @Override
    public BlockingQueue<Runnable> getQueue() {
        return new ArrayDequeToBlockingQueueAdapter<>(taskQueue);
    }


    @Override
    public int getQueueSize() {
        ReentrantReadWriteLock.ReadLock stateLock = this.lock.readLock();
        stateLock.lock();
        try {
            return taskQueue.size();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public int getQueueRemainingCapacity() {
        return getQueueSizeLimit() - getQueueSize();
    }

    @Override
    public int getQueueCapacity() {
        return getQueueSizeLimit();
    }

    static class ArrayDequeToBlockingQueueAdapter<E> implements BlockingQueue<E> {

        private final ReentrantLock lock = new ReentrantLock(true);

        private final ArrayDeque<E> delegate;

        public ArrayDequeToBlockingQueueAdapter(ArrayDeque<E> delegate) {
            this.delegate = delegate;
        }


        @Override
        public boolean add(E e) {
            try {
                this.lock.lock();
                return delegate.add(e);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public boolean offer(E e) {
            try {
                this.lock.lock();
                return delegate.offer(e);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public E remove() {
            try {
                this.lock.lock();
                return delegate.remove();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public E poll() {
            try {
                this.lock.lock();
                return delegate.poll();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public E element() {
            try {
                this.lock.lock();
                return delegate.element();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public E peek() {
            try {
                this.lock.lock();
                return delegate.peek();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public void put(E e) throws InterruptedException {
            try {
                this.lock.lock();
                delegate.push(e);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
            try {
                this.lock.lock();
                return delegate.offer(e);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public E take() throws InterruptedException {
            try {
                this.lock.lock();
                return delegate.poll();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            try {
                this.lock.lock();
                return delegate.poll();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public int remainingCapacity() {
            return -1;
        }

        @Override
        public boolean remove(Object o) {
            try {
                this.lock.lock();
                return delegate.remove(o);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return delegate.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            try {
                this.lock.lock();
                return delegate.addAll(c);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            try {
                this.lock.lock();
                return delegate.removeAll(c);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            try {
                this.lock.lock();
                return delegate.retainAll(c);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public void clear() {
            try {
                this.lock.lock();
                delegate.clear();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return delegate.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return delegate.iterator();
        }

        @Override
        public Object[] toArray() {
            return delegate.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return delegate.toArray(a);
        }

        @Override
        public int drainTo(Collection<? super E> c) {

            try {
                this.lock.lock();
                int size = size();
                c.addAll(delegate);
                delegate.clear();
                return size;
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public int drainTo(Collection<? super E> c, int maxElements) {
            if (maxElements <= 0) {
                return 0;
            }
            try {
                this.lock.lock();
                int size = size();
                int min = Math.min(size, maxElements);

                Iterator<E> it = delegate.iterator();
                for (int i = 0; i < min; i++) {
                    c.add(it.next());
                    it.remove();
                }
                return min;
            } finally {
                this.lock.unlock();
            }
        }
    }

    public static class MutableThreadPoolAdapter extends ThreadPoolExecutor {

        private final MutableThreadPool delegate;


        public MutableThreadPoolAdapter(MutableThreadPool mutableThreadPool) {
            super(mutableThreadPool.getCorePoolSize(), mutableThreadPool.getMaximumPoolSize(), mutableThreadPool.getKeepAliveTime(TimeUnit.SECONDS), TimeUnit.SECONDS, mutableThreadPool.getQueue(), mutableThreadPool.getRejectedExecutionHandler());
            this.delegate = mutableThreadPool;
        }


        @Override
        public void execute(Runnable command) {
            delegate.execute(command);
        }

        @Override
        public void shutdown() {
            delegate.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return delegate.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return delegate.isShutdown();
        }

        @Override
        public boolean isTerminating() {
            return delegate.isTerminating();
        }

        @Override
        public boolean isTerminated() {
            return delegate.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }

        @Override
        protected void finalize() {
            //TODO
            //        super.finalize();
        }

        @Override
        public void setThreadFactory(ThreadFactory threadFactory) {
            delegate.setThreadFactory(threadFactory);
        }

        @Override
        public ThreadFactory getThreadFactory() {
            return delegate.getThreadFactory();
        }

        @Override
        public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
            delegate.setRejectedExecutionHandler(handler);
        }

        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return delegate.getRejectedExecutionHandler();
        }

        @Override
        public void setCorePoolSize(int corePoolSize) {

            delegate.setCorePoolSize(corePoolSize);
        }

        @Override
        public int getCorePoolSize() {
            return delegate.getCorePoolSize();
        }

        @Override
        public boolean prestartCoreThread() {
            //TDOO
            //        return mutableThreadPool.prestartCoreThread();
            return false;
        }

        @Override
        public int prestartAllCoreThreads() {
            return super.prestartAllCoreThreads();
        }

        @Override
        public boolean allowsCoreThreadTimeOut() {
            return delegate.allowsCoreThreadTimeOut();
        }

        @Override
        public void allowCoreThreadTimeOut(boolean value) {
            delegate.allowCoreThreadTimeOut(value);
        }

        @Override
        public void setMaximumPoolSize(int maximumPoolSize) {
            delegate.setMaximumPoolSize(maximumPoolSize);
        }

        @Override
        public int getMaximumPoolSize() {
            return delegate.getMaximumPoolSize();
        }

        @Override
        public void setKeepAliveTime(long time, TimeUnit unit) {
            delegate.setKeepAliveTime(time, unit);
        }

        @Override
        public long getKeepAliveTime(TimeUnit unit) {
            return delegate.getKeepAliveTime(unit);
        }

        @Override
        public BlockingQueue<Runnable> getQueue() {
            delegate.getQueue();
            return delegate.getQueue();
        }

        @Override
        public boolean remove(Runnable task) {
            return super.remove(task);
        }

        @Override
        public void purge() {
            super.purge();
        }

        @Override
        public int getPoolSize() {
            return delegate.getPoolSize();
        }

        @Override
        public int getActiveCount() {
            return delegate.getActiveCount();
        }

        @Override
        public int getLargestPoolSize() {
            return delegate.getLargestPoolSize();
        }

        @Override
        public long getTaskCount() {
            return delegate.getTaskCount();
        }

        @Override
        public long getCompletedTaskCount() {
            return delegate.getCompletedTaskCount();
        }

        @Override
        public String toString() {
            return delegate.toString();
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
        }

        @Override
        protected void terminated() {
            super.terminated();
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
            return super.newTaskFor(runnable, value);
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Future<?> submit(Runnable task) {
            return delegate.submit(task);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return delegate.submit(task, result);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return delegate.submit(task);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return delegate.invokeAny(tasks);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return delegate.invokeAny(tasks, timeout, unit);
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return delegate.invokeAll(tasks);
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return delegate.invokeAll(tasks, timeout, unit);
        }
    }
}
