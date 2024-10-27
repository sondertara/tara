package com.sondertara.common.retry.backoff;

import com.sondertara.common.base.Assert;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.retry.BackoffPolicy;
import com.sondertara.common.retry.RetryConfig;

import java.util.List;
import java.util.stream.Collectors;

public class LoopBackoffPolicy extends BackoffPolicy {
    private List<Long> candidateBackoff;

    public LoopBackoffPolicy() {
        this(Lists.asList(50L, 100L, 200L, 1000L, 2 * 1000L, 5 * 1000L, 30 * 1000L, 60 * 1000L, 120 * 1000L));
    }

    public LoopBackoffPolicy(List<Long> candidateBackoff) {
        this.candidateBackoff = StreamUtils.of(candidateBackoff)
                .filter(value -> value != null && value > 0).collect(Collectors.toList());
    }

    @Override
    protected long getBackoffTimeInternal(RetryConfig config, int attempts) {
         Assert.isTrue(!this.candidateBackoff.isEmpty());
        int index = (attempts - 1) % this.candidateBackoff.size();
        long backoff = this.candidateBackoff.get(index);
        return backoff;
    }
}
