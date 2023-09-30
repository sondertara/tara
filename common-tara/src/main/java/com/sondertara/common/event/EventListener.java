package com.sondertara.common.event;

import com.google.common.eventbus.Subscribe;

public interface EventListener<T extends EventSource> {
    /**
     * the event
     * @param event
     */
    @Subscribe
    void onEvent(T event);

}
