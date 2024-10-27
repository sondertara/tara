package com.sondertara.common.timing.scheduling;

/**
 *  */
public class ImmediateTriggerFactory implements TriggerFactory {
    private static final String NAME = "immediate";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Trigger get(String expression) {
        return new ImmediateTrigger();
    }
}
