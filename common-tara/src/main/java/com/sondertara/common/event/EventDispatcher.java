package com.sondertara.common.event;

/**
 * 只能本地使用
 */
public interface EventDispatcher {
    <T extends DomainEvent<?>> void dispatch(T event, Iterable<EventListener<T>> subscribers);
}
