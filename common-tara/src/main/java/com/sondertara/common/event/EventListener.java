package com.sondertara.common.event;

/**
 * @param <EVENT>
 * @author jinuo.fang
 */
@SuppressWarnings("rawtypes")
public interface EventListener<EVENT extends DomainEvent> extends java.util.EventListener {
    /**
     * 事件
     *
     * @param event
     */
    void onEvent(EVENT event);
}
