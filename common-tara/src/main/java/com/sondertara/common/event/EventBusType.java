package com.sondertara.common.event;

/**
 * @author huangxiaohu
 */

public enum EventBusType {
    /**
     * 事件类型
     */
    ASYNC, SYNC;

    public static EventBusType parse(boolean isAsync) {
        if (isAsync) {
            return EventBusType.ASYNC;
        }
        return EventBusType.SYNC;
    }
}