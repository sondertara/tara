package com.sondertara.common.event;

import com.sondertara.common.concurrent.threadpool.ThreadPoolConfigure;
import com.sondertara.common.concurrent.threadpool.ThreadPoolFactory;
import com.sondertara.common.event.local.AsyncEventRegister;
import com.sondertara.common.event.local.SimpleEventRegister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangxiaohu.1ih
 */
public class DefaultEventBusManager implements EventBusManager {

    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, EventPublisher> SYNC_MAP = new ConcurrentHashMap<>();
    private final Map<String, EventPublisher> ASYNC_MAP = new ConcurrentHashMap<>();

    private final ThreadPoolConfigure configure;

    public DefaultEventBusManager() {
        this.configure = ThreadPoolConfigure.builder()
                .corePoolSize(32)
                .maxPoolSize(64)
                .keepAliveTime(30)
                .allowCoreThreadTimeOut(true)
                .daemon(true)
                .key("TARA_EVENT_BUS")
                .group("Tara-Event")
                .queue(2048)
                .showQueueWarningSize(128)
                .build();
    }


    private EventPublisher findEventBus(String topic, EventBusType type) {
        String identity = "TEB-" + topic;
        switch (type) {
            case SYNC:
                return SYNC_MAP.get(identity);
            case ASYNC:
                return ASYNC_MAP.get(identity);
            default:
                return null;
        }
    }

    private void removeEventBus(String topic, EventBusType type) {
        String identity = "TEB-" + topic;
        switch (type) {
            case SYNC:
                SYNC_MAP.remove(identity);
                break;
            case ASYNC:
                EventPublisher publisher = ASYNC_MAP.remove(identity);
                if (null != publisher) {
                    ThreadPoolFactory.getInstance().shutdown(identity);
                }
                break;
            default:
        }
    }

    private EventPublisher create(String topic, EventBusType type, boolean parallelForListeners) {
        EventPublisher eventEmitter = findEventBus(topic, type);
        if (null == eventEmitter) {
            String identity = "TEB-" + topic;
            try {
                lock.lockInterruptibly();
                switch (type) {
                    case SYNC:
                        eventEmitter = SYNC_MAP.get(identity);
                        if (eventEmitter == null) {
                            CommonEventRegister newEventManager = new SimpleEventRegister();
                            newEventManager.setName(identity);
                            eventEmitter = SYNC_MAP.putIfAbsent(identity, newEventManager);
                            if (eventEmitter == null) {
                                eventEmitter = newEventManager;
                            }
                        }
                        break;
                    case ASYNC:
                        EventPublisher asyncEventManager = ASYNC_MAP.get(identity);
                        if (asyncEventManager == null) {
                            CommonEventRegister newEventManager = createAsync(identity, parallelForListeners);
                            newEventManager.setName(identity);
                            asyncEventManager = ASYNC_MAP.putIfAbsent(identity, newEventManager);
                            if (asyncEventManager == null) {
                                eventEmitter = newEventManager;
                            }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("EventType error");
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        return eventEmitter;
    }

    private CommonEventRegister createAsync(String topic, boolean parallelForListeners) {
        ThreadPoolConfigure cloned = configure.copyTo(ThreadPoolConfigure.class);
        cloned.setKey(topic);
        AsyncEventRegister publisher = new AsyncEventRegister(parallelForListeners);
        publisher.setName(topic);
        publisher.setExecutor(ThreadPoolFactory.getInstance().getOrCreatePool(cloned));
        return publisher;
    }

    @Override
    public <T extends Enum<T>> EventPublisher getEventBus(Class<T> topicClass, boolean parallelForListeners) {

        return create(topicClass.getName(), EventBusType.ASYNC, parallelForListeners);
    }

    @Override
    public EventPublisher getEventBus(String topic, boolean parallelForListeners) {
        return create(topic, EventBusType.ASYNC, parallelForListeners);

    }

    @Override
    public <T extends Enum<T>> void destroy(Class<T> topic) {
        removeEventBus(topic.getName(), EventBusType.SYNC);
        removeEventBus(topic.getName(), EventBusType.ASYNC);

    }

    @Override
    public void destroy(String topic) {
        removeEventBus(topic, EventBusType.SYNC);
        removeEventBus(topic, EventBusType.ASYNC);

    }

    @Override
    public <T extends Enum<T>> EventPublisher getEventBus(Class<T> topicClass) {
        return getEventBus(topicClass, true);
    }

    @Override
    public <T extends Enum<T>> EventPublisher getSyncEventBus(Class<T> tClass) {
        return create(tClass.getName(), EventBusType.SYNC, false);
    }

    @Override
    public EventPublisher getEventBus(String topic) {
        return getEventBus(topic, true);
    }

    @Override
    public EventPublisher getSyncEventBus(String topic) {
        return create(topic, EventBusType.SYNC, false);
    }
}
