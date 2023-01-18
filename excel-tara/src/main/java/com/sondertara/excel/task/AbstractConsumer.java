package com.sondertara.excel.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * abstract consumer
 *
 * @author huangxiaohu
 */
abstract public class AbstractConsumer implements Consumer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);
    private final CountDownLatch countDownLatch;

    public boolean isDone = false;


    private final int threadNum;

    private final Phaser phaser = new Phaser();


    private ThreadPoolExecutor poolExecutor;

    protected AbstractConsumer(CountDownLatch countDownLatch, int threadNum) {
        this.threadNum = threadNum;
        this.countDownLatch = countDownLatch;
        this.poolExecutor = new ThreadPoolExecutor(threadNum, Math.max(Runtime.getRuntime().availableProcessors() * 2 + 1, threadNum), 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Excel-worker-%d").build(), new ThreadPoolExecutor.AbortPolicy());

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

    @Override
    public void init() {
        phaser.register();
        for (int i = 0; i < threadNum; i++) {
            phaser.register();
            poolExecutor.execute(() -> {
                try {
                    consume();
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                logger.debug("Consumer[{}]: task is done...", Thread.currentThread().getName());
                phaser.arrive();
            });
        }
        poolExecutor.execute(() -> {
            phaser.arriveAndAwaitAdvance();
            this.isDone = true;
            logger.info("All Consumers is finish");
            countDownLatch.countDown();
        });

    }

}
