package com.sondertara.common.event.local;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.event.DomainEvent;
import com.sondertara.common.event.EventDispatcher;
import com.sondertara.common.event.EventListener;

/**
 * @author huangxiaohu.1ih
 */
public class SimpleEventDispatcher implements EventDispatcher {
    public static final SimpleEventDispatcher INSTANCE = new SimpleEventDispatcher();


    @Override
    public <T extends DomainEvent<?>> void dispatch(T event, Iterable<EventListener<T>> subscribers) {
        CollectionUtils.forEach(subscribers, eventListener -> eventListener.onEvent(event));
    }
}
