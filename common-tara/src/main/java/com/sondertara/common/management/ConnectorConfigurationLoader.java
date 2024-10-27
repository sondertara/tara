package com.sondertara.common.management;

import com.sondertara.common.configuration.ConfigurationLoader;

public interface ConnectorConfigurationLoader extends ConfigurationLoader<ConnectorConfiguration>{
    @Override
    ConnectorConfiguration load(String id);
}
