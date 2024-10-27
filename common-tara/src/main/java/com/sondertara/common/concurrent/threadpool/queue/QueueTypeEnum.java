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

import com.sondertara.common.concurrent.threadpool.exceptions.ThreadPoolCreateException;
import com.sondertara.common.math.NumberUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;


/**
 * QueueTypeEnum related
 *
 * @author yanhom
 *  **/
@Slf4j
@Getter
public enum QueueTypeEnum {

    /**
     * BlockingQueue type.
     */

    ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue"),

    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue"),

    PRIORITY_BLOCKING_QUEUE(3, "PriorityBlockingQueue"),

    DELAY_QUEUE(4, "DelayQueue"),

    SYNCHRONOUS_QUEUE(5, "SynchronousQueue"),

    LINKED_TRANSFER_QUEUE(6, "LinkedTransferQueue"),

    LINKED_BLOCKING_DEQUE(7, "LinkedBlockingDeque"),
    VARIABLE_LINKED_BLOCKING_QUEUE(8, "VariableLinkedBlockingQueue"),

    MEMORY_SAFE_LINKED_BLOCKING_QUEUE(9, "MemorySafeLinkedBlockingQueue");

    private final Integer code;
    private final String name;

    QueueTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BlockingQueue<Runnable> buildSynchronousQueue(boolean fair) {
        return new SynchronousQueue<>(fair);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockingQueue<Runnable> buildBq(String name, int capacity) {

        QueueTypeEnum queueType = findQueueType(name);
        BlockingQueue<Runnable> blockingQueue = null;

        switch (queueType) {
            case SYNCHRONOUS_QUEUE:
                blockingQueue = new SynchronousQueue<>(false);
                break;
            case DELAY_QUEUE:
                blockingQueue = new DelayQueue();
                break;
            case ARRAY_BLOCKING_QUEUE:
                blockingQueue = new ArrayBlockingQueue<>(capacity);
                break;
            case LINKED_BLOCKING_DEQUE:
                blockingQueue = new LinkedBlockingDeque<>(capacity);
                break;
            case LINKED_BLOCKING_QUEUE:
                blockingQueue = new LinkedBlockingQueue<>(capacity);
                break;
            case LINKED_TRANSFER_QUEUE:
                blockingQueue = new LinkedTransferQueue<>();
                break;
            case PRIORITY_BLOCKING_QUEUE:
                blockingQueue = new PriorityBlockingQueue<>(capacity);
                break;
            case MEMORY_SAFE_LINKED_BLOCKING_QUEUE:
                blockingQueue = new MemorySafeLinkedBlockingQueue<>(capacity);
                break;
            default:
                blockingQueue = new VariableLinkedBlockingQueue<>(capacity);
        }
        return blockingQueue;
    }

    static QueueTypeEnum findQueueType(String codeOrName) {

        boolean isCode = NumberUtils.isCreatable(codeOrName);
        for (QueueTypeEnum typeEnum : values()) {
            if (isCode && typeEnum.getCode().equals(Integer.valueOf(codeOrName))) {
                return typeEnum;
            } else if (!isCode && typeEnum.getName().equals(codeOrName)) {
                return typeEnum;
            }
        }
        log.error("Cannot find specified BlockingQueue {}", codeOrName);
        throw new ThreadPoolCreateException("Cannot find specified BlockingQueue " + codeOrName);
    }
}
