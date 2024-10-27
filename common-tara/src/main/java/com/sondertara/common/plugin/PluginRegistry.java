package com.sondertara.common.plugin;

import com.sondertara.common.registry.Registry;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Predicate;

public interface PluginRegistry extends Registry<String, Plugin> {
    List<Plugin> plugins();

    <P extends Plugin> List<P> find(@NonNull Class<P> itfc);

    <P extends Plugin> List<P> find(@NonNull Predicate<Plugin> predicate);

    <P extends Plugin> List<P> find(@NonNull Class<P> itfc, @NonNull Predicate<P> predicate);

    <P extends Plugin> P findOne(@NonNull Class<P> itfc, @NonNull Predicate<P> predicate);
}
