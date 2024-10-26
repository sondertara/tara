package com.sondertara.excel.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * abstract consumer
 *
 * @author huangxiaohu
 */
abstract public class AbstractConsumer implements Consumer {
    private final static Logger log = LoggerFactory.getLogger(AbstractConsumer.class);

    protected volatile boolean finish = false;
    private final long period;

    protected AbstractConsumer(long period) {

        this.period = period;
    }

    @Override
    public void exit() {
        finish = true;
        finishCallback();
    }

    @Override
    public void init() {

    }

    public boolean isDone() {
        return finish;
    }


    @Override
    public void run() {
        long lastReturnTimestamp = 0;
        while (true) {
            if (finish) {
                break;
            }
            long loop = loop(lastReturnTimestamp);
            lastReturnTimestamp = System.currentTimeMillis();
            if (finish) {
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(loop);
            } catch (InterruptedException e) {
                break;
            }
            if (finish) {
                break;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Excel consumer finish,{}", Thread.currentThread().getName());
        }
    }

    public long loop(long lastReturnTimestamp) {
        consume();
        return period;
    }

    /**
     * notify finish
     */
    public abstract void finishCallback();
}
