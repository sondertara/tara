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

package com.sondertara.common.concurrent.threadpool.queue;

import com.sondertara.common.memory.RamUsageEstimator;
import com.sondertara.common.text.unit.DataSize;

import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Can completely solve the OOM problem caused by {@link java.util.concurrent.LinkedBlockingQueue}.
 *
 * @author huangxiaohu.1ih
 * @see <a href="https://github.com/apache/incubator-shenyu/blob/master/shenyu-common/src/main/java/org/apache/shenyu/common/concurrent/MemorySafeLinkedBlockingQueue.java">MemorySafeLinkedBlockingQueue</a>
 */
public class MemorySafeLinkedBlockingQueue<E> extends VariableLinkedBlockingQueue<E> {

    private static final long serialVersionUID = 8032578371739960142L;


    private int maxMemory;

    public MemorySafeLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    /**
     * default max memory is 512MB
     *
     * @param capacity the capacity
     */
    public MemorySafeLinkedBlockingQueue(final int capacity) {
        super(capacity);
        this.maxMemory = DataSize.ofMegabytes(512).toBytes().intValue();
    }

    public MemorySafeLinkedBlockingQueue(final int capacity, final int maxMemory) {
        super(capacity);
        this.maxMemory = maxMemory;
    }

    public MemorySafeLinkedBlockingQueue(final Collection<? extends E> c, final int maxMemory) {
        super(c);
        this.maxMemory = maxMemory;
    }

    /**
     * set the max free memory.
     *
     * @param maxFreeMemory the max free memory
     */
    public void setMaxMemory(final int maxFreeMemory) {
        this.maxMemory = maxMemory;
    }

    /**
     * get the max free memory.
     *
     * @return the max free memory limit
     */
    public int getMaxMemory() {
        return maxMemory;
    }

    /**
     * determine if there is any remaining free memory.
     *
     * @return true if has free memory
     */
    public boolean hasRemainedMemory() {
        if (RamUsageEstimator.sizeOfCollection(this) > maxMemory) {
            throw new RejectedExecutionException("The thread pool queue is max memory size reached:" + DataSize.ofBytes(this.maxMemory));
        }
        return true;
    }

    @Override
    public void put(final E e) throws InterruptedException {
        if (hasRemainedMemory()) {
            super.put(e);
        }
    }

    @Override
    public boolean offer(final E e, final long timeout, final TimeUnit unit) throws InterruptedException {
        return hasRemainedMemory() && super.offer(e, timeout, unit);
    }

    @Override
    public boolean offer(final E e) {
        return hasRemainedMemory() && super.offer(e);
    }
}
