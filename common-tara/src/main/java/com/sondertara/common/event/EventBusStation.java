package com.sondertara.common.event;

import com.sondertara.common.base.NameAware;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.registry.GenericRegistry;

/**
 *  */
public class EventBusStation extends GenericRegistry<EventBus> implements EventBus, NameAware {
    private String name;
    private EventBusSelector selector = EventBusSelector.SELECT_ALL;

    public void setSelector(EventBusSelector selector) {
        this.selector = selector;
    }

    @Override
    public void publish(final DomainEvent event) {
        CollectionUtils.forEach(selector.apply(instances()), eventBus -> eventBus.publish(event));
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
