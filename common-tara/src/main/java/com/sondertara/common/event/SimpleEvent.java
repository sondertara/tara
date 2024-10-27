package com.sondertara.common.event;


import lombok.Getter;

/**
 * @author huangxiaohu.1ih
 */
public class SimpleEvent<R> extends DomainEvent<String> {
    @Getter
    private final R data;

    public SimpleEvent(String eventType, R data) {
        super();
        setDomain(eventType);
        this.data = data;
        StackTraceElement element = new Throwable().getStackTrace()[1];
        String className = element.getClassName();
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        setSource(className + "#" + methodName + ":" + lineNumber);
    }
}