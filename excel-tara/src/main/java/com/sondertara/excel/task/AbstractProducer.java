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
                    poolExecutor.execute(() -> {
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

    private final CountDownLatch countDownLatch;


    public AtomicBoolean isDone = new AtomicBoolean(false);


    private final int threadNum;

    private final Phaser phaser = new Phaser();

    private final ThreadPoolExecutor poolExecutor;


    AbstractProducer(CountDownLatch countDownLatch, int threadNum) {
        this.threadNum = threadNum;
        this.countDownLatch = countDownLatch;
        this.poolExecutor = new ThreadPoolExecutor(threadNum, Math.max(Runtime.getRuntime().availableProcessors() * 2 + 1, threadNum), 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Excel-worker-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    }


    @Override
    public void init() {

        poolExecutor.execute(this);
    }

    @Override
    public void exit() {
        if (null != poolExecutor) {
            try {
                this.poolExecutor.shutdown();
            } catch (Exception ignore) {

            }
        }
    }

    public boolean isDone() {
        return this.isDone.get();
    }

    /**
     * notify finish
     */
    public abstract void finish();
}