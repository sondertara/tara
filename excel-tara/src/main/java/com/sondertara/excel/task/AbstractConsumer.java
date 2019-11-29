package com.sondertara.excel.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * abstract consumer
 *
 * @author huangxiaohu
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
        logger.info("consumer[{}] task is done...", Thread.currentThread().getName());
    }
}
