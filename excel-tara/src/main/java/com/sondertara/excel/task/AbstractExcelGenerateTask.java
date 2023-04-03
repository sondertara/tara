package com.sondertara.excel.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sondertara.common.exception.TaraException;
import com.sondertara.common.model.PageResult;
import com.sondertara.excel.function.ExportFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * generate csv excel file
 *
 * @param <R> query result
 * @author huangxiaohu
 */
public abstract class AbstractExcelGenerateTask<R> implements TaskRegiser {
    private static final Logger logger = LoggerFactory.getLogger(AbstractExcelGenerateTask.class);
    private final AtomicInteger page = new AtomicInteger(0);

    private final AtomicInteger maxIndex = new AtomicInteger(Integer.MAX_VALUE);
    private final ExportFunction<R> exportFunction;

    private final LinkedBlockingQueue<PageResult<R>> queue;

    private final CountDownLatch countDownLatch = new CountDownLatch(2);

    private int consumers = 3;
    private int producers = Runtime.getRuntime().availableProcessors();


    public AtomicBoolean producerFinish = new AtomicBoolean(false);

    public static final ThreadPoolExecutor SHUTDOWN = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 5, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), new ThreadFactoryBuilder().setNameFormat("te-st%d").build());

    public AbstractExcelGenerateTask(ExportFunction<R> exportFunction) {
        this.exportFunction = exportFunction;
        queue = new LinkedBlockingQueue<>(16);
    }

    public void start() {
        ExcelQueryDataConsumer consumer = new ExcelQueryDataConsumer(consumers);
        consumer.init();
        ExcelQueryDataProducer producer = new ExcelQueryDataProducer(producers);
        producer.init();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            SHUTDOWN.execute(() -> {
                consumer.exit();
                producer.exit();
            });
        }
    }

    /**
     * 解析
     *
     * @param pageResult
     */
    public abstract void parse(PageResult<R> pageResult);


    @Override
    public void consumers(int threadNum) {
        this.consumers = threadNum;
    }

    @Override
    public void producers(int threadNum) {
        this.producers = threadNum;

    }

    private class ExcelQueryDataProducer extends AbstractProducer {


        private ExcelQueryDataProducer(int threadNum) {
            super(countDownLatch, threadNum);
        }

        @Override
        public boolean produce() {
            if (isDone.get()) {
                return false;
            }
            final int queryPage = page.getAndIncrement();
            if (queryPage > maxIndex.get() || isDone.get()) {
                throw new TaraException("Arrived at the lasted page");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("start query page[{}]...", queryPage);
            }
            final PageResult<R> result = exportFunction.query(queryPage);
            maxIndex.compareAndSet(Integer.MAX_VALUE, result.endIndex());
            logger.debug(" producer maxIndex:"+maxIndex);
            if (logger.isDebugEnabled()) {
                logger.debug("end query page[{}]...", queryPage);
            }

            if (result.isEmpty()) {
                logger.debug("query data is empty,query exit[{}] !",queryPage);
                return false;
            }
            try {
                queue.put(result);
                logger.debug("Producer[{}]:current data[index={}]", Thread.currentThread().getName(), queryPage);
                return true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }


        @Override
        public void finish() {
            producerFinish.set(true);
        }
    }

    private class ExcelQueryDataConsumer extends AbstractConsumer {


        private ExcelQueryDataConsumer(int threadNum) {
            super(countDownLatch, threadNum);
        }


        @Override
        public void consume() {
            while (true) {
                try {
                    if (!producerFinish.get() || !queue.isEmpty()) {
                        PageResult<R> result = queue.poll(500, TimeUnit.MILLISECONDS);
                        if (null != result) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Consumer[{}] parse data start[{}]", Thread.currentThread().getName(),result.getPage());
                            }
                            parse(result);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Consumer[{}] parse data end[{}]", Thread.currentThread().getName(), result.getPage());
                            }
                        }
                    } else if (producerFinish.get() && queue.isEmpty()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Consumer finish");
                        }
                        return;
                    }
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    logger.error("Consumer[{}]: get queue error", Thread.currentThread().getName(), e);
                    throw new RuntimeException(e);
                }
            }
        }

    }

}
