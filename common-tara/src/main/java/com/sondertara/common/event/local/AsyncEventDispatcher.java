package com.sondertara.common.event.local;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.concurrent.CommonTask;
import com.sondertara.common.event.DomainEvent;
import com.sondertara.common.event.EventDispatcher;
import com.sondertara.common.event.EventListener;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 *
 */
@SuppressWarnings("ALL")
public class AsyncEventDispatcher implements EventDispatcher {
    private ExecutorService executor;
    /**
     * 多个 Listener时，是并行运行，还是串行运行;
     */
    private boolean parallel = false;

    public AsyncEventDispatcher() {

    }

    public AsyncEventDispatcher(boolean parallel) {
        this.setParallel(parallel);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }


    @Override
    public  <T extends DomainEvent<?>> void dispatch(T event, Iterable<EventListener<T>> subscribers) {
        if (!parallel) {
            this.getExecutor().execute(CommonTask.wrap(new Runnable() {
                @Override
                public void run() {
                    SimpleEventDispatcher.INSTANCE.dispatch(event, subscribers);
                }
            }));
        } else {
            CollectionUtils.forEach(subscribers, new Consumer<EventListener>() {
                @Override
                public void accept(final EventListener eventListener) {
                    AsyncEventDispatcher.this.getExecutor().execute(CommonTask.wrap(new Runnable() {
                        @Override
                        public void run() {
                            eventListener.onEvent(event);
                        }
                    }));
                }
            });
        }
    }
}
