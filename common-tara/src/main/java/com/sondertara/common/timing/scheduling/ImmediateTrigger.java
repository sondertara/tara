package com.sondertara.common.timing.scheduling;

import java.util.concurrent.TimeUnit;

/**
 *  *
 * 立即调度
 */
public class ImmediateTrigger extends PeriodicTrigger {
    public static final ImmediateTrigger INSTANCE = new ImmediateTrigger();
    public ImmediateTrigger() {
        super(0, TimeUnit.MILLISECONDS);
    }
}
