package com.sondertara.common.configuration.database;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.diff.MapDiffResult;
import com.sondertara.common.comparator.Comparators;
import com.sondertara.common.configuration.AbstractConfigurationRepository;
import com.sondertara.common.configuration.Configuration;
import com.sondertara.common.lifecycle.InitializationException;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class DatabaseBasedConfigurationRepository<T extends Configuration> extends AbstractConfigurationRepository<T, DatabaseBasedConfigurationLoader<T>, DatabaseBasedConfigurationWriter<T>> {

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            super.init();
            Objects.requireNonNull(loader, "the configuration load is null");
            Logger logger = Loggers.getLogger(getClass());
            if (writer == null) {
                logger.warn("The writer is not specified for the repository ({}), will disable write configuration to storage", getName());
            }
            // enable refresh
            if (reloadIntervalInSeconds > 1) {
                logger.info("The configuration refresh task is disabled for repository: {}", getName());
            }
            inited = true;
        }
    }

    @Override
    public void reload() {
        Map<String, T> newConfigs = loader.loadAll();
        Map<String, T> oldConfigs = getAll();
        MapDiffResult<String, T> differResult = CollectionUtils.diff(oldConfigs, newConfigs, getComparator(), Comparators.STRING_COMPARATOR);
        if (differResult.hasDifference()) {
            CollectionUtils.forEach(differResult.getAdds(), new Consumer<T>() {
                @Override
                public void accept(T newConfig) {
                    add(newConfig, false);
                }
            });
            CollectionUtils.forEach(differResult.getUpdates(), new Consumer<T>() {
                @Override
                public void accept(T t) {
                    update(t, false);
                }
            });
            CollectionUtils.forEach(differResult.getRemoves(), new Consumer<T>() {
                @Override
                public void accept(T t) {
                    removeById(t.getId());
                }
            });
        }
    }
}
