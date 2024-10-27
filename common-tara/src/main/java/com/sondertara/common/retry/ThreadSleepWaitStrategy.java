package com.sondertara.common.retry;

import com.sondertara.common.base.Valid;

public class ThreadSleepWaitStrategy implements WaitStrategy {
    private long maxMills;
    public ThreadSleepWaitStrategy(){
        this(5*60*1000L);
    }
    public ThreadSleepWaitStrategy(long maxMills){
        Valid.isTrue(maxMills>=0);
        this.maxMills = maxMills;
    }
    @Override
    public void await(long mills) throws InterruptedException{
        Thread.sleep(Math.max(0, Math.min(mills, this.maxMills)));
    }
}
