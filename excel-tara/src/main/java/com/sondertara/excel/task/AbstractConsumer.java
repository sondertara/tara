package com.sondertara.excel.task;

import lombok.extern.slf4j.Slf4j;

/**
 * @author huangxiaohu
 * 抽象消费者
 */
@Slf4j
abstract public class AbstractConsumer implements Consumer, Runnable {
    public boolean isDone = false;

    @Override
    public void run() {
        while (!isDone) {
            try {
                consume();
            } catch (InterruptedException e) {
                log.error("consume task error:", e);
                break;
            }
        }
        log.info("consume task is done.[{}]", Thread.currentThread().getName());
    }
}
