package com.sondertara.common.pipeline;

public class HeadHandlerContext extends HandlerContext {
    public HeadHandlerContext() {
        super(NoopHandler.getInstance());
    }

    public HeadHandlerContext(Handler handler) {
        super(handler);
    }

    @Override
    public boolean hasPrev() {
        return false;
    }
}
