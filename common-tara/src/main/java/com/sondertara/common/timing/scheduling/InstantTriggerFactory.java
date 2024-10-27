package com.sondertara.common.timing.scheduling;

import com.sondertara.common.datetime.LocalDateTimeUtils;

import java.util.Date;

/**
 *
 */
public class InstantTriggerFactory implements TriggerFactory {
    private static final String NAME = "instant";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Trigger get(String expression) {
        Date date = LocalDateTimeUtils.parse(expression);


        return new InstantTrigger(date);
    }
}
