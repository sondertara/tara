package com.sondertara.excel.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sondertara.common.exception.TaraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * abstract producer
 *
 * @author huangxiaohu
 */
abstract class AbstractProducer implements Producer, Runnable {
    @Override
    public void run() {
        logger.info("Producers is started");
        phaser.register();
        try {
            if (produce()) {
                for (int i = 0; i < threadNum - 1; i++) {
                    phaser.register();
                    TASK_POOL.execute(() -> {
                        try {
                            do {
                                try {
                                    Thread.sleep(5);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            } while (produce());
                        } catch (TaraException e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(e.getMessage());
                            }
                        } finally {
                            phaser.arrive();
                        }

                    });
                }
            }
        } finally {
            phaser.arriveAndAwaitAdvance();
            finish();
            this.isDone.set(true);
            logger.info("All producer is finish");
            countDownLatch.countDown();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractProducer.class);

    private static final ThreadPoolExecutor TASK_POOL = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2 + 16, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(30), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Excel-worker-%d").build(), new ThreadPoolExecutor.CallerRunsPolicy());
    private final CountDownLatch countDownLatch;


    public AtomicBoolean isDone = new AtomicBoolean(false);


    private final int threadNum;

    private final Phaser phaser = new Phaser();


    AbstractProducer(CountDownLatch countDownLatch, int threadNum) {
        this.threadNum = threadNum;
        this.countDownLatch = countDownLatch;

    }

    @Override
    public void init() {

        TASK_POOL.execute(this);
    }

    public boolean isDone() {
        return this.isDone.get();
    }

    /**
     * notify finish
     */
    public abstract void finish();
}