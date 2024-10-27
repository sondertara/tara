package com.sondertara.common.configuration;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.NonAbsentHashMap;
import com.sondertara.common.collection.NonDistinctTreeSet;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.comparator.OrderedComparator;
import com.sondertara.common.lifecycle.Lifecycle;
import com.sondertara.common.struct.Holder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MultipleLevelConfigurationRepository<T extends Configuration, Loader extends ConfigurationLoader<T>, Writer extends ConfigurationWriter<T>> extends AbstractConfigurationRepository<T, Loader, Writer> {
    /**
     * key : the repository name
     * value: order
     */
    private Map<String, Integer> delegateOrderMap = new NonAbsentHashMap<String, Integer>(new Function<String, Integer>() {
        @Override
        public Integer apply(String input) {
            return Integer.MAX_VALUE;
        }
    });

    private Function<String, Integer> delegateOrderSupplier = new Function<String, Integer>() {
        @Override
        public Integer apply(String name) {
            return delegateOrderMap.get(name);
        }
    };
    private Comparator<String> orderComparator = (OrderedComparator<String>) s -> delegateOrderSupplier.apply(s);

    /**
     * key: the repository name
     */
    private Map<String, ConfigurationRepository> delegates = new LinkedHashMap<>();

    public void addRepository(ConfigurationRepository repository) {
        this.addRepository(repository, Integer.MAX_VALUE);
    }

    public void addRepository(ConfigurationRepository repository, int order) {
        delegateOrderMap.put(repository.getName(), order);
        delegates.put(repository.getName(), repository);
        // sort
        Set<String> set = new NonDistinctTreeSet<String>(orderComparator);
        set.addAll(delegateOrderMap.keySet());
        final Map<String, ConfigurationRepository> tmp = new LinkedHashMap<>();
        StreamUtils.of(set)
                .map(new Function<String, ConfigurationRepository>() {
                    @Override
                    public ConfigurationRepository apply(String input) {
                        return delegates.get(input);
                    }
                })
                .forEach(new Consumer<ConfigurationRepository>() {
                    @Override
                    public void accept(ConfigurationRepository repository) {
                        tmp.put(repository.getName(), repository);
                    }
                });

        this.delegates = tmp;
    }

    public void removeRepository(String repositoryName) {
        delegateOrderMap.remove(repositoryName);
        delegates.remove(repositoryName);
    }

    @Override
    public void doStart() {

        // reverse it
        List<ConfigurationRepository> list = CollectionUtils.reverse(new ArrayList<>(delegates.values()));
        list.forEach(Lifecycle::startup);
        super.doStart();
    }

    @Override
    public void doStop() {
        super.doStop();
        StreamUtils.of(delegates.values()).forEach(new Consumer<ConfigurationRepository>() {
            @Override
            public void accept(ConfigurationRepository configurationRepository) {
                try {
                    configurationRepository.shutdown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public T getById(final String id) {
        T t = super.getById(id);

        if (t == null) {
            final Holder<T> holder = new Holder<T>();
            CollectionUtils.forEach(delegates.values(), new Consumer<ConfigurationRepository>() {
                @Override
                public void accept(ConfigurationRepository configurationRepository) {
                    holder.set((T) configurationRepository.getById(id));
                }
            }, new Predicate<ConfigurationRepository>() {
                @Override
                public boolean test(ConfigurationRepository configurationRepository) {
                    return !holder.isNull();
                }
            });
            t = holder.get();
        }
        return t;
    }

    public T getById(String repositoryName, String id) {
        ConfigurationRepository repository = delegates.get(repositoryName);
        if (repository != null) {
            return (T) repository.getById(id);
        }
        return null;
    }

    public T getById(final String id, Function<List<T>, T> mapper) {
        return mapper.apply(delegates.keySet().stream().map(repositoryName -> getById(repositoryName, id)).collect(Collectors.toList()));
    }

    @Override
    public void removeById(final String id, final boolean sync) {
        super.removeById(id, sync);
        CollectionUtils.forEach(delegates.values(), new Consumer<ConfigurationRepository>() {
            @Override
            public void accept(ConfigurationRepository configurationRepository) {
                configurationRepository.removeById(id, sync);
            }
        });
    }

    private ConfigurationRepository findFirstRepository(final String id) {
        if (super.getById(id) != null) {
            return this;
        }
        return CollectionUtils.findFirst(delegates.values(), new Predicate<ConfigurationRepository>() {
            @Override
            public boolean test(ConfigurationRepository value) {
                return value.getById(id) != null;
            }
        });
    }


    @Override
    public T add(T configuration, boolean sync) {
        return super.add(configuration, sync);
    }

    @Override
    public void update(T configuration, boolean sync) {
        ConfigurationRepository old = findFirstRepository(configuration.getId());
        if (old != null) {
            old.update(configuration, sync);
        } else {
            super.update(configuration, sync);
        }
    }

    @Override
    public Map<String, T> getAll() {
        final Map<String, T> map = new LinkedHashMap<>();
        CollectionUtils.reverse(new ArrayList<>(delegates.values())).forEach(c->map.putAll(c.getAll()));
        return map;
    }
}
