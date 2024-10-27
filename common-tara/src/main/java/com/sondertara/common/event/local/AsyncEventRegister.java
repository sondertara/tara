package com.sondertara.common.event.local;

import com.sondertara.common.event.CommonEventRegister;

import java.util.concurrent.ExecutorService;

public class AsyncEventRegister extends CommonEventRegister {
    public AsyncEventRegister(boolean parallelForListeners) {
        setDispatcher(new AsyncEventDispatcher(parallelForListeners));
        setName("Async-EventBus-" + COUNTER.getAndIncrement() + (parallelForListeners ? "parallel" : "serial"));
    }

    public ExecutorService getExecutor() {
        return ((AsyncEventDispatcher) getDispatcher()).getExecutor();
    }

    public void setExecutor(ExecutorService executor) {
        ((AsyncEventDispatcher) this.getDispatcher()).setExecutor(executor);
    }
}
