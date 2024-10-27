package com.sondertara.common.timing.cron;

import com.sondertara.common.timing.scheduling.Trigger;
import com.sondertara.common.timing.scheduling.TriggerFactory;

/**
 *  */
public class CronTriggerFactory implements TriggerFactory {
    private static final String NAME = "cron";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Trigger get(String expression) {
        return new CronTrigger(expression);
    }
}
