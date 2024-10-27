package com.sondertara.common.plugin;


import com.sondertara.common.collection.StreamUtils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SimpleLoadablePluginRegistry extends SimplePluginRegistry {
    private PluginLoader pluginLoader;

    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    @Override
    public <P extends Plugin> List<P> find(Class<P> itfc) {
        Iterator<P> plugins = this.pluginLoader.load(itfc);
       StreamUtils.of(plugins).filter(new Predicate<P>() {
            @Override
            public boolean test(P plugin) {
                return !contains(plugin.getName());
            }
        }).forEach(new Consumer<P>() {
            @Override
            public void accept(P p) {
                register(p);
            }
        });
        return super.find(itfc);
    }
}
