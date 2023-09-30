package com.sondertara.common.event;

import com.google.common.eventbus.EventBus;

public class DefaultEventBusHolder<T extends EventSource> implements EventBusHolder<T> {

    private final EventBus eventBus;

    public DefaultEventBusHolder(String id){
        this.eventBus = new EventBus(id);
    }

    @Override
    public void registerListener(EventListener<T> listener){
        this.eventBus.register(listener);
    }

    @Override
    public void publishEvent(T event){
        this.eventBus.post(event);
    }
}
