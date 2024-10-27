package com.sondertara.common.progress;

import com.sondertara.common.event.EventPublisher;
import com.sondertara.common.event.EventPublisherAware;
import com.sondertara.common.event.local.SimpleEventRegister;
import com.sondertara.common.lifecycle.AbstractInitializable;
import com.sondertara.common.lifecycle.InitializationException;
import com.sondertara.common.registry.GenericRegistry;
import org.jspecify.annotations.NonNull;

/**
 *  */
public class ProgressTracer extends AbstractInitializable implements EventPublisherAware {
    @NonNull
    private EventPublisher eventPublisher;
    @NonNull
    private GenericRegistry<ProgressSource> tracing;


    @Override
    protected void doInit() throws InitializationException {
        if(eventPublisher==null){
            setEventPublisher(new SimpleEventRegister());
        }
        if(tracing==null){
            setTracing(new GenericRegistry<ProgressSource>());
        }
    }

    public void begin(ProgressSource source) {
        if (!tracing.names().contains(source.getName())) {
            tracing.register(source);
        }
        ProgressEvent event = new ProgressEvent(ProgressEventType.START, source);
        eventPublisher.publish(event);

    }

    public void finish(ProgressSource source) {
        this.tracing.unregister(source.getName());
        ProgressEvent event = new ProgressEvent(ProgressEventType.FINISH, source);
        eventPublisher.publish(event);
    }

    public void updateProcess(ProgressSource source) {
        if (this.tracing.contains(source.getName())) {
            ProgressEvent event = new ProgressEvent(ProgressEventType.UPDATE, source);
            eventPublisher.publish(event);
        }
    }


    @Override
    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        this.eventPublisher = publisher;
    }

    public GenericRegistry<ProgressSource> getTracing() {
        return tracing;
    }

    public void setTracing(GenericRegistry<ProgressSource> sourceRegistry) {
        this.tracing = sourceRegistry;
    }

}
