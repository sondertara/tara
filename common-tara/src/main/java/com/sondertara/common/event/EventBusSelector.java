package com.sondertara.common.event;

import java.util.List;
import java.util.function.Function;

/**
 *  */
public interface EventBusSelector extends Function<List<EventBus>, List<EventBus>> {
    List<EventBus> apply(List<EventBus> candidateBuses);

    EventBusSelector SELECT_ALL = new EventBusSelector() {
        @Override
        public List<EventBus> apply(List<EventBus> candidateBuses) {
            return candidateBuses;
        }
    };
}
