package com.sondertara.common.pipeline.simplex;

import com.sondertara.common.pipeline.AbstractHandler;
import com.sondertara.common.pipeline.HandlerContext;

public class SimplexToDuplexHandlerAdapter extends AbstractHandler {
    private SimplexHandler handler;

    public SimplexToDuplexHandlerAdapter(SimplexHandler handler){
        this.handler = handler;
    }

    @Override
    public void inbound(HandlerContext ctx) throws Throwable {
        Object result = handler.apply(ctx.getTarget());
        ctx.getCurrentValueHolder().set(result);
        super.inbound(ctx);
    }
}
