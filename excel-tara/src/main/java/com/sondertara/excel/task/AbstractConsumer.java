package com.sondertara.excel.task;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangxiaohu
 * 抽象消费者
 */
abstract public class AbstractConsumer implements Consumer, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);

    public boolean isDone = false;

    @Override
    public void run() {
        while (!isDone) {
            try {
                consume();
            } catch (InterruptedException e) {
                logger.error("consume task error:", e);
                break;
            }
        }
        logger.info("consume task is done.[{}]", Thread.currentThread().getName());
    }
}
