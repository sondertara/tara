package com.sondertara.common.retry.backoff;

import com.sondertara.common.retry.BackoffPolicy;
import com.sondertara.common.retry.RetryConfig;

/**
 * 指数级
 */
public class ExponentialBackoffPolicy extends BackoffPolicy {
    public static final ExponentialBackoffPolicy INSTANCE = new ExponentialBackoffPolicy();

    @Override
    public long getBackoffTimeInternal(RetryConfig config, int attempts) {
        long backoffTime = (long) (config.getSleepInterval() * Math.pow(2, attempts));
        return backoffTime;
    }
}