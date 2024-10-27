package com.sondertara.common.event;

/**
 *  */
public interface IpcEventBus extends EventBus {
    @Override
    void publish(DomainEvent event);
}
