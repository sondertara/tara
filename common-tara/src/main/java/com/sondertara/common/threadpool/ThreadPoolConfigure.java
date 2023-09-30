
package com.sondertara.common.threadpool;

import com.sondertara.common.threadpool.queue.QueueTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class ThreadPoolConfigure {
    private final String key;
    private final BlockingQueue<Runnable> queue;
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final long threadTimeout;

    private final int showThreadQueueSize;

    private long createTime;

    private boolean scheduledPool = false;

    public void setScheduledPool(boolean scheduledPool) {
        this.scheduledPool = scheduledPool;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    private final RejectedExecutionHandler rejectedExecutionHandler;


    ThreadPoolConfigure(final String key,
                        final boolean scheduledPool, final BlockingQueue<Runnable> queue,
                        final int corePoolSize, final int maxPoolSize, final long keepAliveTime,
                        final long timeout, final int showThreadQueueSize,
                        final RejectedExecutionHandler rejectedExecutionHandler) {
        this.key = key;
        this.queue = queue;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.threadTimeout = timeout;
        this.showThreadQueueSize = showThreadQueueSize;
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        this.scheduledPool = scheduledPool;
    }

    public boolean isScheduledPool() {
        return scheduledPool;
    }

    public static ThreadPoolConfigureBuilder builder() {
        return new ThreadPoolConfigureBuilder();
    }

    public String getKey() {
        return this.key;
    }

    public BlockingQueue<Runnable> getQueue() {
        return this.queue;
    }

    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public long getKeepAliveTime() {
        return this.keepAliveTime;
    }

    public long getThreadTimeout() {
        return this.threadTimeout;
    }



    public int getShowThreadQueueSize() {
        return this.showThreadQueueSize;
    }


    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return this.rejectedExecutionHandler;
    }

    public static class ThreadPoolConfigureBuilder {
        private String key = "Tara-Pool";
        private BlockingQueue<Runnable> queue;
        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        private long keepAliveTime = 120L;

        private long threadTimeout = -1L;
        private int showThreadQueueSize = -1;
        private boolean scheduledPool;
        private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();

        ThreadPoolConfigureBuilder() {
        }

        public ThreadPoolConfigureBuilder key(final String key) {
            this.key = key;
            return this;
        }

        public ThreadPoolConfigureBuilder scheduledPool(final boolean scheduledPool) {
            this.scheduledPool = scheduledPool;
            return this;
        }


        public ThreadPoolConfigureBuilder queue(final BlockingQueue<Runnable> queue) {
            this.queue = queue;
            return this;
        }

        public ThreadPoolConfigureBuilder corePoolSize(final int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public ThreadPoolConfigureBuilder maxPoolSize(final int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public ThreadPoolConfigureBuilder keepAliveTime(final long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public ThreadPoolConfigureBuilder threadTimeout(final long timeout) {
            this.threadTimeout = timeout;
            return this;
        }


        public ThreadPoolConfigureBuilder showThreadQueueSize(final int showThreadQueueSize) {
            this.showThreadQueueSize = showThreadQueueSize;
            return this;
        }


        public ThreadPoolConfigureBuilder rejectedExecutionHandler(final RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

        public ThreadPoolConfigure build() {
            if (null == queue) {
                this.queue = QueueTypeEnum.buildQueue(QueueTypeEnum.LINKED_BLOCKING_QUEUE.getName(), 8192);
            }
            if (Math.abs(this.queue.remainingCapacity() - Integer.MAX_VALUE) < 64) {
                log.error("Thread pool configure queue seems like an unbounded queue,please set the capacity");

            }

            if (this.showThreadQueueSize == -1) {
                this.showThreadQueueSize = (int) ((this.queue.size() + this.queue.remainingCapacity()) * 0.8);
            }

            return new ThreadPoolConfigure(this.key, this.scheduledPool, this.queue, this.corePoolSize, this.maxPoolSize, this.keepAliveTime, this.threadTimeout, this.showThreadQueueSize, this.rejectedExecutionHandler);
        }


        @Override
        public String toString() {
            return "ThreadPoolConfigure.ThreadPoolConfigureBuilder(key=" + this.key + ", queue=" + this.queue + ", corePoolSize=" + this.corePoolSize + ", maxPoolSize=" + this.maxPoolSize + ", keepAliveTime=" + this.keepAliveTime + ", threadTimeout=" + this.threadTimeout + ", queueSize=" + this.queue.size() + this.queue.remainingCapacity() + ", showThreadQueueSize=" + this.showThreadQueueSize + ", rejectedExecutionHandler=" + this.rejectedExecutionHandler + ")";
        }
    }
}
