package com.sondertara.common.plugin;

import com.sondertara.common.spi.CommonServiceProvider;

import java.util.Iterator;

public class SpiPluginLoader<T extends Plugin<?>> implements PluginLoader<T> {
    private volatile CommonServiceProvider<T> serviceProvider;

    public void setServiceProvider(CommonServiceProvider<T> serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public Iterator<T> load(Class<T> pluginClass) {
        if (this.serviceProvider == null) {
            this.serviceProvider = new CommonServiceProvider<>();
        }
        return serviceProvider.get(pluginClass);
    }
}
