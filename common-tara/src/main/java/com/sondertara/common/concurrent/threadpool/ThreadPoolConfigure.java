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

import com.sondertara.common.base.Assert;
import com.sondertara.common.concurrent.threadpool.em.RejectedTypeEnum;
import com.sondertara.common.concurrent.threadpool.queue.QueueTypeEnum;
import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.common.text.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * ThreadPool base properties, mainly used for adapter module.
 *
 * @author yanhom
 *  **/
@Data
@Slf4j
public class ThreadPoolConfigure implements Serializable {

    /**
     * Name of thread pool
     */
    private String key;


    protected String group;


    protected long createTime;

    /**
     * BlockingQueue capacity.
     */
    private int queueCapacity = 8192;
    private String queueType = QueueTypeEnum.VARIABLE_LINKED_BLOCKING_QUEUE.getName();

    private boolean fair = false;


    /**
     * Simple Alias Name of  ThreadPool. Use for notify.
     */
    private String threadPoolAliasName;

    protected int corePoolSize =4;
    protected int maximumPoolSize = 4*2+1;

    /**
     * When the number of threads is greater than the core,
     * this is the maximum time that excess idle threads
     * will wait for new tasks before terminating.
     */
    protected long keepAliveTimeSec = 60;

    protected boolean allowCoreThreadTimeOut = false;


    protected boolean daemon = false;
    protected boolean scheduledPool = false;

    /**
     * If enable notify.
     */
    private boolean notifyEnabled = true;

    /**
     * Task execute timeout, unit (ms), just for statistics.
     */
    protected int runTimeout = 0;

    /**
     * Task queue wait timeout, unit (ms), just for statistics.
     */
    private long queueTimeout = 0;

    protected int showQueueWarningSize;


    private String rejectedHandlerName = RejectedTypeEnum.DISCARD_POLICY.getName();


    public ThreadPoolConfigure() {

    }

    public void init() {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Thread Pool name must not be null");
        }
        if (corePoolSize > maximumPoolSize) {
            throw new IllegalArgumentException(StringUtils.format("Thread pool(name={}) max size({}) is less than core size({})...", key, maximumPoolSize, corePoolSize));
        }
        if (group == null) {
            group = key;
        }
        //do nothing

    }

    ThreadPoolConfigure(String group, String key, int corePoolSize, int maxPoolSize, int keepAliveTimeSec, boolean allowCoreThreadTimeOut, QueueTypeEnum queueType, int queueCapacity, int showQueueWarningSize, int runTimeout, boolean daemon, boolean fair, boolean scheduledPool, RejectedTypeEnum rejectedType) {
        this.key = key;
        this.group = group;
        this.queueCapacity = queueCapacity;
        this.queueType = queueType.getName();

        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maxPoolSize;
        this.keepAliveTimeSec = keepAliveTimeSec;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        this.daemon = daemon;
        this.scheduledPool = scheduledPool;
        this.fair = fair;
        //this.queueTimeout
        this.runTimeout = runTimeout;
        if (showQueueWarningSize < 1) {
            this.showQueueWarningSize = (int) Math.ceil(this.getQueueCapacity() * 0.25);
        } else {
            this.showQueueWarningSize = showQueueWarningSize;
        }
        this.rejectedHandlerName = rejectedType.getName();
        this.createTime = System.currentTimeMillis();
    }

    public <T extends ThreadPoolConfigure> T copyTo(Class<T> tClass) {

        T props = ReflectUtils.newInstance(tClass);
        props.setGroup(this.getGroup());
        props.setKey(this.getKey());
        props.setCreateTime(this.getCreateTime());
        props.setQueueCapacity(this.getQueueCapacity());
        props.setQueueType(this.getQueueType());
        props.setFair(this.isFair());
        props.setThreadPoolAliasName(this.getThreadPoolAliasName());
        props.setCorePoolSize(this.getCorePoolSize());
        props.setMaximumPoolSize(this.getMaximumPoolSize());
        props.setKeepAliveTimeSec(getKeepAliveTimeSec());
        props.setAllowCoreThreadTimeOut(this.isAllowCoreThreadTimeOut());
        props.setDaemon(isDaemon());
        props.setScheduledPool(isScheduledPool());
        props.setNotifyEnabled(isNotifyEnabled());
        props.setRunTimeout(getRunTimeout());
        props.setQueueTimeout(getQueueTimeout());
        props.setShowQueueWarningSize(getShowQueueWarningSize());
        props.setRejectedHandlerName(getRejectedHandlerName());
        props.init();
        return props;

    }

    public static ThreadPoolConfigureBuilder builder() {
        return new ThreadPoolConfigureBuilder();
    }


    public static class ThreadPoolConfigureBuilder {
        private String key = "Tara-Pool";

        private int queueCapacity = 8192;
        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        private int maxPoolSize;
        private int keepAliveTime = 30;

        private QueueTypeEnum queueType = null;

        private int threadRunTimeout = -1;
        private int showQueueWarningSize = 0;
        private boolean scheduledPool;
        private RejectedTypeEnum rejectedType = RejectedTypeEnum.DISCARD_POLICY;
        private RejectedExecutionHandler rejectedHandler;
        private boolean daemon = false;
        private String group = "Tara";
        private boolean fair = false;
        private boolean allowCoreThreadTimeOut = false;
        private ThreadFactory threadFactory;

        ThreadPoolConfigureBuilder() {
        }


        public ThreadPoolConfigureBuilder daemon(final boolean daemon) {
            this.daemon = daemon;
            return this;
        }
        public ThreadPoolConfigureBuilder threadFactory(final ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public ThreadPoolConfigureBuilder queue(final QueueTypeEnum queueType, int capacity) {
            Assert.isNull(this.queueType, "The queue type is already set to {} by other method", this.queueType);
            this.queueType = queueType;
            this.queueCapacity = capacity;
            return this;
        }

        public ThreadPoolConfigureBuilder queue(int capacity) {
            Assert.isNull(this.queueType, "The queue type is already set to {} by other method", this.queueType);
            this.queueType = QueueTypeEnum.VARIABLE_LINKED_BLOCKING_QUEUE;
            this.queueCapacity = capacity;
            return this;
        }

        public ThreadPoolConfigureBuilder syncQueue(final boolean fair) {
            Assert.isNull(this.queueType, "The queue type is already set to {} by other method", this.queueType);
            this.queueType = QueueTypeEnum.SYNCHRONOUS_QUEUE;
            this.fair = fair;
            return this;
        }


        public ThreadPoolConfigureBuilder key(final String key) {
            this.key = key;
            return this;
        }

        public ThreadPoolConfigureBuilder allowCoreThreadTimeOut(final boolean allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
            return this;
        }

        public ThreadPoolConfigureBuilder group(final String key) {
            this.group = key;
            return this;
        }

        public ThreadPoolConfigureBuilder scheduledPool(final boolean scheduledPool) {
            this.scheduledPool = scheduledPool;
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

        public ThreadPoolConfigureBuilder keepAliveTime(final int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public ThreadPoolConfigureBuilder threadRunTimeout(final int timeout) {
            this.threadRunTimeout = timeout;
            return this;
        }

        public ThreadPoolConfigureBuilder showQueueWarningSize(final int showQueueWarningSize) {
            this.showQueueWarningSize = showQueueWarningSize;
            return this;
        }


        public ThreadPoolConfigureBuilder rejectedHandler(final RejectedTypeEnum rejectedHandler) {
            this.rejectedType = rejectedHandler;
            return this;
        }
        public <T extends RejectedExecutionHandler> ThreadPoolConfigureBuilder  rejectedHandler(final RejectedExecutionHandler rejectedHandler) {
            this.rejectedHandler = rejectedHandler;
            return this;
        }

        public ThreadPoolConfigure build() {
            if (null == queueType) {
                this.queueType = QueueTypeEnum.VARIABLE_LINKED_BLOCKING_QUEUE;
            }
            if (maxPoolSize == 0) {
                maxPoolSize = corePoolSize * 2 + 1;
            } else if (maxPoolSize < corePoolSize) {
                maxPoolSize = corePoolSize;
            }
            return new ThreadPoolConfigure(group, key, corePoolSize, maxPoolSize, keepAliveTime, allowCoreThreadTimeOut, queueType, queueCapacity, showQueueWarningSize, threadRunTimeout, daemon, fair, scheduledPool, rejectedType);
        }

    }
}
