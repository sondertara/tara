package com.sondertara.excel.task;

import lombok.extern.slf4j.Slf4j;

/**
 * @author huangxiaohu
 * 抽象生产者
 */
@Slf4j
abstract class AbstractProducer implements Producer, Runnable {
    public boolean isDone = false;

    @Override
    public void run() {
        while (!isDone) {
            try {
                Thread.sleep(3);
                produce();
            } catch (InterruptedException e) {
                log.error("produce task error:", e);
                break;
            }
        }

        log.info("produce task is done.[{}]", Thread.currentThread().getName());
    }
}