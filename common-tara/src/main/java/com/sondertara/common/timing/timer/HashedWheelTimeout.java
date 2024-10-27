package com.sondertara.common.timing.timer;

import com.sondertara.common.reflect.ReflectUtils;


public class HashedWheelTimeout extends AbstractTimeout implements Runnable {

    // remainingRounds will be calculated and set by Worker.transferTimeoutsToBuckets() before the
    // HashedWheelTimeout will be added to the correct HashedWheelBucket.
    long remainingRounds;

    // This will be used to chain timeouts in HashedWheelTimerBucket via a double-linked-list.
    // As only the workerThread will act on it there is no need for synchronization / volatile.
    HashedWheelTimeout next;
    HashedWheelTimeout prev;

    // The bucket to which the timeout was added
    HashedWheelBucket bucket;

    public HashedWheelTimeout(HashedWheelTimer timer, TimerTask task, long deadline) {
        super(timer, task, deadline);
    }

    @Override
    public Timer timer() {
        return timer;
    }

    @Override
    public TimerTask task() {
        return task;
    }

    @Override
    public boolean cancel() {
        // only update the state it will be removed from HashedWheelBucket on next tick.
        try {
            return compareAndSetState(ST_INIT, ST_CANCELLED) || compareAndSetState(ST_EXPIRED, ST_CANCELLED);
        } finally {
            // If a task should be canceled we put this to another queue which will be processed on each tick.
            // So this means that we will have a GC latency of max. 1 tick duration which is good enough. This way
            // we can make again use of our MpscLinkedQueue and so minimize the locking / overhead as much as possible.
            ((HashedWheelTimer)timer).cancelledTimeouts.add(this);
        }
    }

    void remove() {
        HashedWheelBucket bucket = this.bucket;
        if (bucket != null) {
            bucket.remove(this);
        } else {
            ((HashedWheelTimer)timer).pendingTimeouts.decrementAndGet();
        }
    }




    @Override
    public String toString() {
        final long currentTime = System.nanoTime();
        long remaining = deadline - currentTime + ((HashedWheelTimer)timer).startTime;

        StringBuilder buf = new StringBuilder(192)
                .append(ReflectUtils.getSimpleClassName(this))
                .append('(')
                .append("deadline: ");
        if (remaining > 0) {
            buf.append(remaining)
                    .append(" ns later");
        } else if (remaining < 0) {
            buf.append(-remaining)
                    .append(" ns ago");
        } else {
            buf.append("now");
        }

        if (isCancelled()) {
            buf.append(", cancelled");
        }

        return buf.append(", task: ")
                .append(task())
                .append(')')
                .toString();
    }
}