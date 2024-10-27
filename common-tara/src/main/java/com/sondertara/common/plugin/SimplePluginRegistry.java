package com.sondertara.common.plugin;

import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.registry.GenericRegistry;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SimplePluginRegistry extends GenericRegistry<Plugin> implements PluginRegistry {
    @Override
    public List<Plugin> plugins() {
        return this.instances();
    }

    @Override
    public <P extends Plugin> List<P> find(Class<P> itfc) {
        return find(obj -> itfc.isInstance(obj));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <P extends Plugin> List<P> find(Predicate<Plugin> predicate) {
        return this.plugins().stream().filter(predicate).map(s -> (P) s).collect(Collectors.toList());
    }

    @Override
    public final <P extends Plugin> List<P> find(Class<P> itfc, Predicate<P> predicate) {
        return StreamUtils.of(find(itfc)).filter(predicate).collect(Collectors.toList());
    }

    @Override
    public final <P extends Plugin> P findOne(Class<P> itfc, Predicate<P> predicate) {
        return find(itfc, predicate).stream().findFirst().get();
    }
}
