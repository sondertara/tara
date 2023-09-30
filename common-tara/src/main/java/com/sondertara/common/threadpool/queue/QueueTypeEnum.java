package com.sondertara.common.threadpool.queue;

import com.sondertara.common.exception.TaraException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

@Slf4j
@Getter
public enum QueueTypeEnum {

    /**
     * BlockingQueue type.
     */
    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue"),

    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue"),

    PRIORITY_BLOCKING_QUEUE("PriorityBlockingQueue"),

    DELAY_QUEUE("DelayQueue"),

    SYNCHRONOUS_QUEUE("SynchronousQueue"),

    LINKED_TRANSFER_QUEUE("LinkedTransferQueue"),

    LINKED_BLOCKING_DEQUE("LinkedBlockingDeque"),

    VARIABLE_LINKED_BLOCKING_QUEUE("VariableLinkedBlockingQueue"),

    MEMORY_SAFE_LINKED_BLOCKING_QUEUE("MemorySafeLinkedBlockingQueue");


    private final String name;

    QueueTypeEnum(String name) {
        this.name = name;
    }

    public static BlockingQueue<Runnable> buildQueue(String name, int capacity) {
        QueueTypeEnum queueType = from(name);

        BlockingQueue<Runnable> blockingQueue = null;
        if (Objects.equals(queueType, ARRAY_BLOCKING_QUEUE)) {
            blockingQueue = new ArrayBlockingQueue<>(capacity);
        } else if (Objects.equals(queueType, LINKED_BLOCKING_QUEUE)) {
            blockingQueue = new LinkedBlockingQueue<>(capacity);
        } else if (Objects.equals(queueType, PRIORITY_BLOCKING_QUEUE)) {
            blockingQueue = new PriorityBlockingQueue<>(capacity);
        } else if (Objects.equals(queueType, DELAY_QUEUE)) {
            blockingQueue = new DelayQueue();
        } else if (Objects.equals(queueType, SYNCHRONOUS_QUEUE)) {
            blockingQueue = new SynchronousQueue<>(true);
        } else if (Objects.equals(queueType, LINKED_TRANSFER_QUEUE)) {
            blockingQueue = new LinkedTransferQueue<>();
        } else if (Objects.equals(queueType, LINKED_BLOCKING_DEQUE)) {
            blockingQueue = new LinkedBlockingDeque<>(capacity);
        } else if (Objects.equals(queueType, VARIABLE_LINKED_BLOCKING_QUEUE)) {
            blockingQueue = new VariableLinkedBlockingQueue<>(capacity);
        } else if (Objects.equals(queueType, MEMORY_SAFE_LINKED_BLOCKING_QUEUE)) {
            blockingQueue =
                    new MemorySafeLinkedBlockingQueue<>(capacity, 256 * 1024 * 1024);
        }
        if (blockingQueue != null) {
            return blockingQueue;
        }

        log.error("Cannot find specified BlockingQueue {}", queueType);
        throw new TaraException("Cannot find specified BlockingQueue " + queueType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockingQueue<Runnable> buildLbq(
            QueueTypeEnum queueType, int capacity, boolean fair, int maxFreeMemory) {
        BlockingQueue<Runnable> blockingQueue = null;
        if (Objects.equals(queueType, ARRAY_BLOCKING_QUEUE)) {
            blockingQueue = new ArrayBlockingQueue<>(capacity);
        } else if (Objects.equals(queueType, LINKED_BLOCKING_QUEUE)) {
            blockingQueue = new LinkedBlockingQueue<>(capacity);
        } else if (Objects.equals(queueType, PRIORITY_BLOCKING_QUEUE)) {
            blockingQueue = new PriorityBlockingQueue<>(capacity);
        } else if (Objects.equals(queueType, DELAY_QUEUE)) {
            blockingQueue = new DelayQueue();
        } else if (Objects.equals(queueType, SYNCHRONOUS_QUEUE)) {
            blockingQueue = new SynchronousQueue<>(fair);
        } else if (Objects.equals(queueType, LINKED_TRANSFER_QUEUE)) {
            blockingQueue = new LinkedTransferQueue<>();
        } else if (Objects.equals(queueType, LINKED_BLOCKING_DEQUE)) {
            blockingQueue = new LinkedBlockingDeque<>(capacity);
        } else if (Objects.equals(queueType, VARIABLE_LINKED_BLOCKING_QUEUE)) {
            blockingQueue = new VariableLinkedBlockingQueue<>(capacity);
        } else if (Objects.equals(queueType, MEMORY_SAFE_LINKED_BLOCKING_QUEUE)) {
            blockingQueue =
                    new MemorySafeLinkedBlockingQueue<>(capacity, maxFreeMemory * 1024 * 1024);
        }
        if (blockingQueue != null) {
            return blockingQueue;
        }

        log.error("Cannot find specified BlockingQueue {}", queueType);
        throw new IllegalArgumentException("Cannot find specified BlockingQueue " + queueType);
    }

    public static QueueTypeEnum from(String name) {
        for (QueueTypeEnum value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No queue found by name:" + name);
    }
}
