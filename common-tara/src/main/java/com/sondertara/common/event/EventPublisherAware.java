package com.sondertara.common.event;

public interface EventPublisherAware {
    EventPublisher getEventPublisher();

    void setEventPublisher(EventPublisher publisher);
}
