package com.sondertara.common.timing.scheduling;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.lifecycle.InitializationException;
import com.sondertara.common.registry.GenericRegistry;

import java.util.ServiceLoader;

/**
 *  */
public class TriggerFactoryRegistry extends GenericRegistry<TriggerFactory> {

    public static final TriggerFactoryRegistry GLOBAL_TRIGGER_REGISTRY;
    static {
        TriggerFactoryRegistry r = new TriggerFactoryRegistry();
        r.init();
        GLOBAL_TRIGGER_REGISTRY = r;
    }
    @Override
    protected void doInit() throws InitializationException {
        StreamUtils.of(ServiceLoader.load(TriggerFactory.class))
                .forEach(triggerFactory -> {
                    if (StringUtils.isNotBlank(triggerFactory.getName())) {
                        register(triggerFactory);
                    }
                });
    }
}
