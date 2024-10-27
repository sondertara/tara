package com.sondertara.excel.task;

import com.sondertara.common.concurrent.threadpool.MetricThreadPoolExecutor;
import com.sondertara.common.concurrent.threadpool.ThreadPoolConfigure;
import com.sondertara.common.concurrent.threadpool.ThreadPoolFactory;
import com.sondertara.common.concurrent.threadpool.em.RejectedTypeEnum;
import com.sondertara.common.id.IndexNumberUtils;
import com.sondertara.excel.base.TaraExcelConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/14 10:15
 */
@Slf4j
public abstract class AbstractTaskExecutor<T extends PageVisitor> implements TaskController {
    private final AtomicInteger page = new AtomicInteger(0);

    private final AtomicInteger maxIndex = new AtomicInteger(Integer.MAX_VALUE);


    private final DelayQueue<T> queue;

    private int consumers = 1;
    private int producers = TaraExcelConfig.CONFIG.getExcelProducerThread();


    private final AtomicInteger producerFinishCount = new AtomicInteger(0);
    private final AtomicInteger consumerFinishCount = new AtomicInteger(0);

    @Getter
    private final AtomicInteger visitedIndex = new AtomicInteger(0);


    public static final MetricThreadPoolExecutor SHUTDOWN = ThreadPoolFactory.getInstance().getOrCreatePool(
            ThreadPoolConfigure.builder()
                    .key("Tara-Excel-Clear")
                    .corePoolSize(Runtime.getRuntime().availableProcessors())
                    .maxPoolSize(Runtime.getRuntime().availableProcessors())
                    .allowCoreThreadTimeOut(true)
                    .queue(500)
                    .rejectedHandler(RejectedTypeEnum.DISCARD_POLICY)
                    .build()
    );

    public AbstractTaskExecutor() {
        queue = new DelayQueue<>();
    }

    public void start() {
        try {
            List<CompletableFuture<Void>> tasks = new ArrayList<>();
            //第一次直接查询
            ExcelQueryDataProducer producer = new ExcelQueryDataProducer();
            producer.produce();
            if (producer.isDone()) {
                log.warn("End of producer,only one query executed.");
                new ExcelQueryDataConsumer().consume();
            } else {
                String executorName = "Tara-Excel-" + IndexNumberUtils.incrementAndGet("Tara-Excel");
                Executor threadPoolExecutor = ThreadPoolFactory.getInstance().newFixedThreadPool(executorName, consumers + producers);
                try {
                    CompletableFuture.runAsync(producer, threadPoolExecutor);
                    for (int i = 0; i < Math.max(0, producers - 1); i++) {
                        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(new ExcelQueryDataProducer(), threadPoolExecutor);
                        tasks.add(cf1);
                    }
                    for (int i = 0; i < consumers; i++) {
                        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(new ExcelQueryDataConsumer(), threadPoolExecutor);
                        tasks.add(cf1);
                    }
                    CompletableFuture<Void> allOf = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
                    allOf.join();
                } finally {
                    SHUTDOWN.execute(() -> {
                        ThreadPoolFactory.getInstance().shutdown(executorName);
                    });
                }
            }
        } catch (Exception e) {
            log.error("Produce-Comsume task execute error,{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @NonNull
    protected abstract T produceData(int index);

    protected abstract void consumeData(@NonNull T data);

    @Override
    public void consumers(int threadNum) {
        this.consumers = threadNum;
    }

    @Override
    public void producers(int threadNum) {
        this.producers = threadNum;

    }

    void finish(int type) {
        if (type == 0) {
            producerFinishCount.incrementAndGet();
        } else {
            consumerFinishCount.incrementAndGet();
        }
    }

    private class ExcelQueryDataProducer extends AbstractProducer {


        private ExcelQueryDataProducer() {
            super(300);
        }


        @Override
        public void produce() {
            if (isDone()) {
                log.info("is Done");
                return;
            }
            final int queryPage = page.getAndIncrement();
            if (queryPage > maxIndex.get()) {
                exit();
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("start query page[{}]...", queryPage);
            }
            T data = produceData(queryPage);
            maxIndex.compareAndSet(Integer.MAX_VALUE, data.maxIndex());
            queue.put(data);
            log.info(" producer maxIndex:" + maxIndex);
            if (log.isDebugEnabled()) {
                log.debug("end query page[{}]...", queryPage);
            }

            if (data.currentIndex() == maxIndex.get()) {
                log.debug("query data is empty,query exit[{}] !", queryPage);
                exit();
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("Producer[{}]:current data[index={}]", Thread.currentThread().getName(), queryPage);
            }
        }

        @Override
        public void finishCallback() {
            finish(0);
        }
    }

    public boolean produceFinish() {
        return producerFinishCount.get() == producers;
    }

    public boolean consumerFinish() {
        return consumerFinishCount.get() == consumers;
    }

    private class ExcelQueryDataConsumer extends AbstractConsumer {


        private ExcelQueryDataConsumer() {
            super(3);
        }


        @Override
        public void consume() {
            try {
                T data = queue.poll(200, TimeUnit.MILLISECONDS);
                if (null != data) {
                    int pageNo = data.currentIndex();
                    visitedIndex.compareAndSet(pageNo, pageNo + 1);
                    log.info("Consumer[{}] parse data start[{}]", Thread.currentThread().getName(), pageNo);
                    consumeData(data);
                    if (log.isDebugEnabled()) {
                        log.debug("Consumer[{}] parse data end[{}]", Thread.currentThread().getName(), pageNo);
                    }
                }
                if (visitedIndex.get() == maxIndex.get() + 1) {
                    if (log.isDebugEnabled()) {
                        log.debug("Last index  has been consumed,the consumer is completed");
                    }
                    exit();
                    return;
                }
                if (produceFinish() && queue.isEmpty()) {
                    if (log.isDebugEnabled()) {
                        log.debug("The data queue is empty,the consumer is completed");
                    }
                    exit();
                    return;
                }
            } catch (InterruptedException e) {
                log.error("Consumer[{}]: get queue error", Thread.currentThread().getName(), e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public void finishCallback() {
            finish(1);
        }
    }
}
