package com.sondertara.common.timing.timer;

/**
 *  * @param <TIMER>
 * @param <TIMEOUT>
 */
public interface TimeoutFactory<TIMER extends Timer, TIMEOUT extends Timeout> {
    TIMEOUT create(TIMER timer, TimerTask task, long deadline);
}
