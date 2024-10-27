package com.sondertara.common.pipeline;

import com.sondertara.common.annotation.Prototype;
import com.sondertara.common.struct.Holder;

@Prototype
public interface Pipeline<T> {
    void addFirst(Handler handler);

    void addLast(Handler handler);

    HeadHandlerContext getHead();

    /**
     * clear all handlers
     */
    void clear();

    /**
     * clear handlers, but head, tail will not be remove
     */
    void reset();

    void bindTarget(T target);

    void unbindTarget();

    T getTarget();

    Holder<T> getTargetHolder();

    void inbound() throws Throwable;

    void inbound(T message) throws Throwable;

    void outbound() throws Throwable;

    boolean hadOutbound();

    HandlerContext getCurrentHandlerContext();
    void setCurrentHandlerContext(HandlerContext handlerContext);
}
