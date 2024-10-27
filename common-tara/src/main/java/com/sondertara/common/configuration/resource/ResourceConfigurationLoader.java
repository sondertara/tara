package com.sondertara.common.configuration.resource;

import com.sondertara.common.base.Valid;
import com.sondertara.common.configuration.AbstractConfigurationLoader;
import com.sondertara.common.configuration.Configuration;
import com.sondertara.common.configuration.InputStreamConfigurationParser;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;
import com.sondertara.common.io.resource.ResourceLocationProvider;
import com.sondertara.common.io.resource.Resources;
import com.sondertara.common.logging.Loggers;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ResourceConfigurationLoader<T extends Configuration> extends AbstractConfigurationLoader<T> {

    private InputStreamConfigurationParser<T> parser;
    private ResourceLocationProvider<String> resourceLocationProvider;


    public InputStreamConfigurationParser<T> getParser() {
        return parser;
    }

    public void setParser(InputStreamConfigurationParser<T> parser) {
        this.parser = parser;
    }

    public ResourceLocationProvider<String> getResourceLocationProvider() {
        return resourceLocationProvider;
    }

    public void setResourceLocationProvider(ResourceLocationProvider<String> resourceLocationProvider) {
        this.resourceLocationProvider = resourceLocationProvider;
    }

    @Override
    public T load(@NonNull String configurationId) {
        Valid.notNull(configurationId, "the configuration id is null or empty");
        Location location = resourceLocationProvider.get(configurationId);
        Objects.requireNonNull(location, "Can't find the location for configuration:"+configurationId );
        T configuration;
        Resource resource = Resources.loadResource(location);
        Logger logger = Loggers.getLogger(getClass());
        if (resource != null && resource.exists()) {
            InputStream inputStream = null;
            try {
                inputStream = resource.getInputStream();
                configuration = parser.parse(inputStream);
                if (configuration != null) {
                    configuration.setId(configurationId);
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                IOUtils.close(inputStream);
            }
        } else {
            logger.error("Location {} is not exists", location);
        }
        return null;
    }
}
