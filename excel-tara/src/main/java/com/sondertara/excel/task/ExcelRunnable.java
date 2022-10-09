package com.sondertara.excel.task;

import java.util.concurrent.CyclicBarrier;

/**
 * @author huangxiaohu
 *         线程工厂
 */
public interface ExcelRunnable {
    /**
     * @param cyclicBarrier thread complete notify
     * @return 生产者
     */
    Runnable newRunnableConsumer(CyclicBarrier cyclicBarrier);

    /**
     * @param cyclicBarrier thread complete notify
     * @return 消费者
     */
    Runnable newRunnableProducer(CyclicBarrier cyclicBarrier);
}
