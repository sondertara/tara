package com.sondertara.common.event;

import java.util.List;

/**
 * <pre>
 * 1、event publisher 本身不需要进行设计，因为只有调用的地方才能被称为 publisher；
 *
 * 2、这里的 event publisher 只是为了 应对在 一个Java进程内的 将event 调度给 listener的过程；
 * 也只用于在一个虚拟机内部；不作为IPC间使用；
 *
 * 3、 addListener 只有在 EventPublisher接口才有的，EventBus 上不能有，也不该有；
 * </pre>
 *
 */
public interface EventPublisher extends EventBus{
    /**
     * 注册监听器
     * @param eventDomain 事件类型
     * @param listener 监听器
     * @param <T> 事件type
     */

   <T extends DomainEvent<?>>  EventListener<T>  addEventListener(String eventDomain, EventListener<T> listener);
   <T extends DomainEvent<?>>  EventListener<T>  addEventListener(ChannelEventListener<T> listener);

    <T extends DomainEvent<?>>    EventListener<T>  addFirst(String eventDomain, EventListener<T> listener);

    /**
     * 移除事件监听器
     * @param eventDomain 事件类型
     * @param listener 监听器
     * @param <T> 事件type
     */

    <T extends DomainEvent<?>>  void removeEventListener(String eventDomain, EventListener<T> listener);
    <T extends DomainEvent<?>>  void removeEventListener(ChannelEventListener<T> listener);

    /**
     * 获取所有监听器
     * @param eventDomain 事件类型
     * @return
     * @param <T> 事件type
     */

    <T extends DomainEvent<?>>  List<EventListener<T>> getListeners(String eventDomain);


}
