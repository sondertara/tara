package com.sondertara.common.event;

import com.sondertara.common.base.NameAware;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.Sets;
import com.sondertara.common.collection.WrappedNonAbsentMap;
import com.sondertara.common.struct.counter.AtomicIntegerCounter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author huangxiaohu.1ih
 */
public abstract class CommonEventRegister implements EventPublisher, NameAware {
    private EventDispatcher dispatcher;
    private String name;
    protected static final AtomicIntegerCounter COUNTER = new AtomicIntegerCounter(0);

    public EventDispatcher getDispatcher() {
        return dispatcher;
    }

    public CommonEventRegister() {
        setName("Common-EventBus");
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public <T> void publish(DomainEvent<T> event) {
        List<EventListener<DomainEvent<?>>> listeners = getListeners(event.getDomain());
        getDispatcher().dispatch(event, listeners);
    }

    @Override
    public String getName() {
        return this.name;
    }


    public void setDispatcher(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    private WrappedNonAbsentMap<String, Set<EventListener<? extends DomainEvent>>> listenerMap = CollectionUtils.wrapAsNonAbsentMap(new ConcurrentHashMap<>(), input -> new LinkedHashSet<>());

    @Override
    public <T extends DomainEvent<?>> EventListener<T> addEventListener(ChannelEventListener<T> listener) {
        for (String eventDomain : listener.channel()) {
            listenerMap.get(eventDomain).add(listener);
        }

        return listener;
    }

    @Override
    public <T extends DomainEvent<?>> EventListener<T> addEventListener(String eventDomain, EventListener<T> listener) {
        listenerMap.get(eventDomain).add(listener);
        return listener;
    }

    @Override
    public <T extends DomainEvent<?>> EventListener<T> addFirst(String eventDomain, EventListener<T> listener) {
        Set<EventListener<? extends DomainEvent>> listeners = listenerMap.get(eventDomain);
        List<EventListener<? extends DomainEvent>> list = Lists.asList(listeners);
        list.add(listener);
        list.addAll(listeners);
        listeners = Sets.asSet(list);
        listenerMap.put(eventDomain, listeners);
        return listener;
    }

    @Override
    public <T extends DomainEvent<?>> void removeEventListener(ChannelEventListener<T> listener) {
        for (String eventDomain : listener.channel()) {
            listenerMap.get(eventDomain).remove(listener);
        }
    }

    @Override
    public <T extends DomainEvent<?>> void removeEventListener(String eventDomain, EventListener<T> listener) {
        listenerMap.get(eventDomain).remove(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent<?>> List<EventListener<T>> getListeners(String eventDomain) {
        Set<EventListener<? extends DomainEvent>> set = listenerMap.get(eventDomain);
        Set<EventListener<? extends DomainEvent>> anyListeners = listenerMap.get("*");
        set.addAll(anyListeners);
        if (CollectionUtils.isEmpty(set)) {
            return new ArrayList<>();
        }
        return set.stream().map(listener -> (EventListener<T>) listener).collect(Collectors.toList());
    }

    public static void main(String[] args) throws InterruptedException {

        EventPublisher eventBus = EventBusManager.INSTANCE.getEventBus("MyTest");
        EventListener<DomainEvent<?>> listener = eventBus.addEventListener("test", System.out::println);
        eventBus.addEventListener("*", event -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("test222");


        });


        eventBus.removeEventListener("test", listener);

        eventBus.publish(new DomainEvent<>("test1", "test"));
        eventBus.publish(new TestEvent("test", "test111"));

//
//        while (true){
//            Thread.sleep(1000*20);
//        }
    }

    public static class TestEvent extends DomainEvent<String> {
        public TestEvent(String domain, String data) {
            super(domain, data);
        }

    }
}
