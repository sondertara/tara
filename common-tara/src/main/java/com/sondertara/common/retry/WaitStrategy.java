package com.sondertara.common.retry;

public interface WaitStrategy {
    void await(long mills) throws InterruptedException;
}
