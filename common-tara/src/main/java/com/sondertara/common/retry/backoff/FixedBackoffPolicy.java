package com.sondertara.common.retry.backoff;

import com.sondertara.common.retry.BackoffPolicy;

public class FixedBackoffPolicy extends BackoffPolicy {
    public static final FixedBackoffPolicy INSTANCE = new FixedBackoffPolicy();
}
