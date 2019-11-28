package com.sondertara.excel.task;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangxiaohu
 * 抽象生产者
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

        logger.info("produce task is done.[{}]", Thread.currentThread().getName());
    }
}