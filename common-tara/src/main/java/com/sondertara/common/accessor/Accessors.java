package com.sondertara.common.accessor;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.CollectionUtils;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 *  */
@SuppressWarnings("ALL")
public class Accessors {

    private Accessors() {
    }

    private static final Map<Class, AccessorFactory> accessorFactoryRegistry = new LinkedHashMap<Class, AccessorFactory>();

    static {
        CollectionUtils.forEach(ServiceLoader.load(AccessorFactory.class), new Consumer<AccessorFactory>() {
            @Override
            public void accept(AccessorFactory accessorFactory) {
                register(accessorFactory);
            }
        });
    }

    public static void register(final AccessorFactory accessorFactory) {
        final List<Class> classes = accessorFactory.applyTo();
        if (ObjectUtils.isNotEmpty(classes)) {
            CollectionUtils.forEach(classes, new Consumer<Class>() {
                @Override
                public void accept(Class aClass) {
                    accessorFactoryRegistry.put(aClass, accessorFactory);
                }
            });

        }
    }

    public static <T> AccessorFactory<T> findFactory(@NonNull final Class klass) {
        Objects.requireNonNull(klass);

        AccessorFactory factory = accessorFactoryRegistry.get(klass);
        if (factory == null) {
            Set<Class> classes = accessorFactoryRegistry.keySet();
            Class matched = CollectionUtils.findFirst(classes, new Predicate<Class>() {
                @Override
                public boolean test(Class expectedClass) {
                    return accessorFactoryRegistry.get(expectedClass).appliable(expectedClass, klass);
                }
            });
            if (matched != null) {
                factory = accessorFactoryRegistry.get(matched);
            }
        }
        return factory;
    }

    public static <T> Accessor<String, T> of(@NonNull final Class klass) {
        AccessorFactory<T> factory = findFactory(klass);
        if (factory == null) {
            return null;
        }
        return factory.get(klass);
    }

    public static <T> Accessor<String, T> of(@NonNull T object) {
        Objects.requireNonNull(object);
        Accessor<String, T> accessor = of(object.getClass());
        if (accessor != null) {
            accessor.setTarget(object);
        }
        return accessor;
    }

}
