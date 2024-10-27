package com.sondertara.common.event;

/**
 * @param <R> the event obj
 * @author huangxiaohu
 */
public interface ChannelEventListener< R extends DomainEvent> extends EventListener<R> {
    /**
     * the event channels to handle event
     *
     * @return arrays of channel
     */
    String[] channel();
}
