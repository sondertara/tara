package com.sondertara.excel.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * abstract producer
 *
 * @author huangxiaohu
 */
abstract class AbstractProducer implements Producer {

    private final static Logger log = LoggerFactory.getLogger(AbstractProducer.class);

    private final long loopPeriod;

    private volatile boolean finish = false;

    AbstractProducer(long loopPeriod) {
        this.loopPeriod = loopPeriod;
    }


    @Override
    public void init() {

    }

    @Override
    public void exit() {
        finish = true;
        finishCallback();
    }

    @Override
    public void run() {
        long lastReturnTimestamp = 0;
        while (true) {
            if (isDone()) {
                break;
            }
            long loop = loop(lastReturnTimestamp);
            lastReturnTimestamp = System.currentTimeMillis();
            if (isDone()) {
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(loop);
            } catch (InterruptedException e) {
                break;
            }
            if (isDone()) {
                break;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Excel producer finish,{}", Thread.currentThread().getName());
        }
    }

    public long loop(long lastReturnTimestamp) {
        produce();
        return loopPeriod;
    }

    public boolean isDone() {
        return finish;
    }

    /**
     * notify finish
     */
    public abstract void finishCallback();
}