package com.sondertara.common.retry;

import com.sondertara.common.base.Assert;
import com.sondertara.common.concurrent.threadlocal.GlobalThreadLocalMap;

public abstract class BackoffPolicy {

    public final long getBackoffTime(RetryConfig config, int attempts) {
        long backoffTime = getBackoffTimeInternal(config, attempts);
         Assert.isTrue(backoffTime >= 0L, "invalid backoff");
        long backoffTimeMils = config.getTimeUnit().toMillis(backoffTime);
        backoffTimeMils = addJitter(backoffTimeMils, config.getJitter());
        long maxSleepTimeMils = config.getMaxSleepTime() > 0 ? config.getTimeUnit().toMillis(config.getMaxSleepTime()) : -1L;
        backoffTimeMils = maxSleepTimeMils > 0 ? Math.min(backoffTimeMils, maxSleepTimeMils) : backoffTimeMils;
        return backoffTimeMils;
    }

    protected long getBackoffTimeInternal(RetryConfig config, int attempts) {
        long backoffTime = config.getSleepInterval();
        return backoffTime;
    }

    private long addJitter(long interval, float jitter) {
        long jitterInterval = (long) (interval * GlobalThreadLocalMap.getRandom().nextFloat() * jitter);
        return interval + jitterInterval;
    }
}
