package com.sondertara.excel.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * abstract producer
 * 
 * @author huangxiaohu
 *
 */
abstract class AbstractProducer implements Producer, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProducer.class);

    public boolean isDone = false;

    @Override
    public void run() {
        while (!isDone) {
            try {
                Thread.sleep(3);
                produce();
            } catch (InterruptedException e) {
                logger.error("produce task error:", e);
                break;
            }
        }

        logger.info("producer[{}] task is done...", Thread.currentThread().getName());
    }
}