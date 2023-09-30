package com.sondertara.common.event;

public interface EventBusHolder<T extends EventSource> {

    void registerListener(EventListener<T> listener);

    void publishEvent(T event);
}
