package com.sondertara.common.concurrent.threadpool.configure;

import com.sondertara.common.concurrent.threadpool.ThreadPoolConfigure;
import com.sondertara.common.concurrent.threadpool.em.RejectedTypeEnum;
import com.sondertara.common.concurrent.threadpool.queue.QueueTypeEnum;
import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * @author huangxiaohu
 */
@Getter
public class TpExecutorProperties extends ThreadPoolConfigure {


    private BlockingQueue<Runnable> queue;
    private RejectedExecutionHandler rejectedExecutionHandler;

    @Override
    public void init() {
        super.init();
        String queueType = getQueueType();
        if (queueType == null) {
            setQueueType(QueueTypeEnum.VARIABLE_LINKED_BLOCKING_QUEUE.getName());
        }
        if (Objects.requireNonNull(queueType).equals(QueueTypeEnum.SYNCHRONOUS_QUEUE.getName()) || queueType.equals(QueueTypeEnum.SYNCHRONOUS_QUEUE.getCode().toString())) {
            this.queue = QueueTypeEnum.buildSynchronousQueue(isFair());
        } else {
            this.queue = QueueTypeEnum.buildBq(queueType, getQueueCapacity());
        }
        this.rejectedExecutionHandler = RejectedTypeEnum.buildRejectedHandler(getRejectedHandlerName(), getKey());
    }
}
