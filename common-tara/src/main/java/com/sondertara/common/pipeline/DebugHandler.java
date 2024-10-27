package com.sondertara.common.pipeline;

import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

public class DebugHandler extends AbstractHandler {

    @Override
    public void inbound(HandlerContext ctx)throws Throwable {
        Logger logger = Loggers.getLogger(getClass());
        if (logger.isDebugEnabled()) {
            logger.debug("inbounding, context: {}", ctx);
        }
        super.inbound(ctx);
    }

    @Override
    public void outbound(HandlerContext ctx) throws Throwable {
        Logger logger = Loggers.getLogger(getClass());
        if (logger.isDebugEnabled()) {
            logger.debug("outbounding, context: {}", ctx);
        }
        super.outbound(ctx);
    }
}
