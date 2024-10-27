package com.sondertara.common.progress;

import com.sondertara.common.event.DomainEvent;

/**
 *  */
public class ProgressEvent extends DomainEvent<ProgressSource> {
    private ProgressEventType eventType;

    public ProgressEvent(ProgressEventType eventType, ProgressSource progressSource) {
        super(progressSource.getEventDomain(), progressSource);
        setEventType(eventType);
    }

    public ProgressEventType getEventType() {
        return eventType;
    }

    public void setEventType(ProgressEventType eventType) {
        this.eventType = eventType;
    }

}
