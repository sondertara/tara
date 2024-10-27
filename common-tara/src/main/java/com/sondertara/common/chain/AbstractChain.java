package com.sondertara.common.chain;

/**
 * @author huangxiaohu.1ih
 */
public abstract class AbstractChain<REQ, RESP> implements Chain<REQ, RESP> {
    private ChainContext context;

    @Override
    public ChainContext getContext() {
        return context;
    }

    @Override
    public void setContext(ChainContext context) {
        this.context = context;
    }
}
