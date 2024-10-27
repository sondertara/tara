package com.sondertara.common.timing.timer;

import java.util.concurrent.TimeUnit;

public interface WaitStrategy {

    /**
     * Wait until the given deadline, deadlineNanoseconds
     *
     * @param deadlineNanoseconds deadline to wait for, in milliseconds
     * @throws InterruptedException if interrupted
     */
    public void await(long deadlineNanoseconds) throws InterruptedException;

    /**
     * Yielding wait strategy.
     * <p>
     * Spins in the loop, until the deadline is reached. Releases the flow control
     * by means of Thread.yield() call. This strategy is less precise than BusySpin
     * one, but is more scheduler-friendly.
     */
    public static class YieldingWait implements WaitStrategy {

        @Override
        public void await(long deadline) throws InterruptedException {
            while (deadline >= System.nanoTime()) {
                Thread.yield();
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
            }
        }
    }

    /**
     * BusySpin wait strategy.
     * <p>
     * Spins in the loop until the deadline is reached. In a multi-core environment,
     * will occupy an entire core. Is more precise than Sleep wait strategy, but
     * consumes more resources.
     */
    public static class BusySpinWait implements WaitStrategy {

        @Override
        public void await(long deadline) throws InterruptedException {
            while (deadline >= System.nanoTime()) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
            }
        }
    }

    /**
     * Sleep wait strategy.
     * <p>
     * Will release the flow control, giving other threads a possibility of execution
     * on the same processor. Uses less resources than BusySpin wait, but is less
     * precise.
     */
    public static class SleepWait implements WaitStrategy {

        @Override
        public void await(long deadline) throws InterruptedException {
            long sleepTimeNanos = deadline - System.nanoTime();
            TimeUnit.NANOSECONDS.wait(sleepTimeNanos);

        }
    }
}
