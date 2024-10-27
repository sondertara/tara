package com.sondertara.common.event;

/**
 * @author huangxiaohu
 */
public interface EventBusManager {

    static EventBusManager INSTANCE = new DefaultEventBusManager();

    /**
     * ff
     *
     * @param topic
     */
    <T extends Enum<T>> void destroy(Class<T> topic);

    void destroy(String topic);


    <T extends Enum<T>> EventPublisher getEventBus(Class<T> topicClass, boolean parallelForListeners);

    <T extends Enum<T>> EventPublisher getEventBus(Class<T> topicClass);


    <T extends Enum<T>> EventPublisher getSyncEventBus(Class<T> tClass);

    EventPublisher getEventBus(String topic, boolean parallelForListeners);

    EventPublisher getEventBus(String topic);


    EventPublisher getSyncEventBus(String topic);
}
