package com.sondertara.common.pipeline;


public interface Handler {
    void inbound(HandlerContext ctx) throws Throwable;

    void outbound(HandlerContext ctx) throws Throwable;
}
