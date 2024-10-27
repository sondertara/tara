package com.sondertara.common.plugin;

import java.util.Iterator;

public interface PluginLoader<T extends Plugin<?>> {
    Iterator<T> load(Class<T> pluginClass);
}
