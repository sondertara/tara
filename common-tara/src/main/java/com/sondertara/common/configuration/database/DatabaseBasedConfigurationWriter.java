package com.sondertara.common.configuration.database;

import com.sondertara.common.configuration.Configuration;
import com.sondertara.common.configuration.ConfigurationWriter;

public abstract class DatabaseBasedConfigurationWriter<T extends Configuration> implements ConfigurationWriter<T> {
    @Override
    public boolean isSupportsWrite() {
        return false;
    }

    @Override
    public boolean isSupportsRewrite() {
        return false;
    }

    @Override
    public boolean isSupportsRemove() {
        return false;
    }

}
