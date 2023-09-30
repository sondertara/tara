package com.sondertara.common.event;

import com.google.common.eventbus.AsyncEventBus;
import com.sondertara.common.threadpool.DefaultThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author huangxiaohu
 */
public class AsyncEventBusHolder<T extends EventSource> implements EventBusHolder<T> {

    private final AsyncEventBus asyncEventBus;


    public AsyncEventBusHolder(String id) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(32, 64, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(4096), new DefaultThreadFactory("Joby-AsyncEventBus"), new ThreadPoolExecutor.DiscardOldestPolicy());

        this.asyncEventBus = new AsyncEventBus(id, executor);
    }

    @Override
    public void registerListener(EventListener<T> listener) {
        this.asyncEventBus.register(listener);
    }

    @Override
    public void publishEvent(T event) {
        this.asyncEventBus.post(event);
    }
}
