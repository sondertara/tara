package com.sondertara.excel.task;

/**
 * @author huangxiaohu
 * 线程工厂
 */
public interface ExcelRunnable {
    /**
     * @return 生产者
     */
    Runnable newRunnableConsumer();

    /**
     * @return 消费者
     */
    Runnable newRunnableProducer();
}
