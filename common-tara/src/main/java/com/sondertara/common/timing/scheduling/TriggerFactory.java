package com.sondertara.common.timing.scheduling;

import com.sondertara.common.base.Named;
import com.sondertara.common.function.Factory;

/**
 *  */
public interface TriggerFactory extends Factory<String, Trigger>, Named {
    @Override
    String getName();

    @Override
    Trigger get(String expression);
}
