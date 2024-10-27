package com.sondertara.common.id.snowflake;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class SnowflakeIdWorkerProviderLoader {
    private static final SnowflakeIdWorkerProvider DEFAULT_PROVIDER = new SystemEnvironmentSnowflakeIdWorkerProvider();
    private static volatile boolean loaded = false;

    private static final Map<String, SnowflakeIdWorkerProvider> LOADED_PROVIDER_MAP = new HashMap<String, SnowflakeIdWorkerProvider>();
    private SnowflakeIdWorkerProviderLoader(){

    }
    public static SnowflakeIdWorkerProvider getProvider() {
        if (!loaded) {
            synchronized (SnowflakeIdWorkerProviderLoader.class) {
                if (!loaded) {
                    ServiceLoader<SnowflakeIdWorkerProvider> loader = ServiceLoader.load(SnowflakeIdWorkerProvider.class);
                    CollectionUtils.forEach(loader, new Consumer<SnowflakeIdWorkerProvider>() {
                        @Override
                        public void accept(SnowflakeIdWorkerProvider provider) {
                            if (provider.getProviderId().equals(SystemEnvironmentSnowflakeIdWorkerProvider.SYSTEM_ENVIRONMENT_SNOWFLAKE)) {
                                LOADED_PROVIDER_MAP.put(provider.getProviderId(), provider);
                            }
                        }
                    });
                    loaded = true;
                    Logger logger = Loggers.getLogger(SnowflakeIdWorkerProviderLoader.class);
                    if (LOADED_PROVIDER_MAP.isEmpty()) {
                        logger.warn("Has not any SnowflakeIdWorkerProvider found, will use the SystemEnvironmentSnowflakeIdWorkerProvider: {}", LOADED_PROVIDER_MAP.keySet());
                    } else if (LOADED_PROVIDER_MAP.size() > 1) {
                        logger.warn("Too many SnowflakeIdWorkerProvider instances found: {}, will use the first", LOADED_PROVIDER_MAP.keySet());
                    }
                }
            }
        }
        if (LOADED_PROVIDER_MAP.isEmpty()) {
            return DEFAULT_PROVIDER;
        }
        return LOADED_PROVIDER_MAP.get(Lists.asList(LOADED_PROVIDER_MAP.keySet()).get(0));
    }

}
