package com.sondertara.common.configuration.manual;

import com.sondertara.common.configuration.AbstractConfigurationRepository;
import com.sondertara.common.configuration.Configuration;
import com.sondertara.common.configuration.ConfigurationWriter;
import com.sondertara.common.configuration.NoopConfigurationLoader;

public class ManualConfigurationRepository<T extends Configuration> extends AbstractConfigurationRepository<T, NoopConfigurationLoader<T>, ConfigurationWriter<T>> {

}
