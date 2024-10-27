package com.sondertara.common.configuration;

import com.sondertara.common.base.Reloadable;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.event.EventPublisher;
import com.sondertara.common.lifecycle.AbstractLifecycle;
import com.sondertara.common.logging.Loggers;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *  */
@SuppressWarnings("rawtypes")
public abstract class BaseConfigurationRepository <T extends Configuration, Loader extends ConfigurationLoader<T>, Writer extends ConfigurationWriter<T>> extends AbstractLifecycle implements ConfigurationRepository<T, Loader, Writer>, Reloadable {
    @NonNull
    protected Loader loader;
    @Nullable
    protected Writer writer;
    @Nullable
    protected EventPublisher eventPublisher;

    @Nullable
    protected ConfigurationEventFactory<T> eventFactory;


    protected Comparator<T> comparator;

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public Comparator<T> getComparator() {
        return comparator;
    }


    public void setEventFactory(ConfigurationEventFactory<T> eventFactory) {
        this.eventFactory = eventFactory;
    }

    @Override
    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        this.eventPublisher = publisher;
    }



    @Override
    protected void doStop() {
        Logger logger = Loggers.getLogger(getClass());
        logger.info("Shutdown configuration repository: {}", getName());
    }

    @Override
    public void setConfigurationLoader(Loader loader) {
        this.loader = loader;
    }

    @Override
    public Loader getConfigurationLoader() {
        return this.loader;
    }

    @Override
    public Writer getConfigurationWriter() {
        return this.writer;
    }

    @Override
    public void setConfigurationWriter(Writer writer) {
        this.writer = writer;
    }


    @Override
    public void removeById(String id) {
        removeById(id, true);
    }


    @Override
    public void add(T configuration) {
        add(configuration, true);
    }


    @Override
    public void update(T configuration) {
        update(configuration, true);
    }


    protected void logMutation(ConfigurationEventType eventType, T configuration) {
        Logger logger = Loggers.getLogger(getClass());
        if (logger.isInfoEnabled()) {
            String template = eventFactory == null ? " a configuration: {}" : (StringUtils.startsWithVowelLetter(eventFactory.getDomain()) ? " an {} configuration: {}" : " a {} configuration: {}");
            template = StringUtils.upperCase(eventType.name().toLowerCase(), 0, 1) + template;
            if (eventFactory != null) {
                logger.info(template, eventFactory.getDomain(), configuration);
            } else {
                logger.info(template, configuration);
            }
        }
    }

    @Override
    protected void doInit() {
        Objects.requireNonNull(getName(), "Repository has no named");
        Logger logger = Loggers.getLogger(getClass());
        logger.info("Initial configuration repository: {}", getName());
    }


    @Override
    public void reload() {
        final Logger logger = Loggers.getLogger(getClass());
        logger.info("Reload repository {}", getName());
        if (loader != null) {
            Map<String, T> all = loader.loadAll();
            if (all != null) {
                StreamUtils.of(all.values())
                        .forEach(new Consumer<T>() {
                            @Override
                            public void accept(T t) {
                                T old = getById(t.getId());
                                if (old != null) {
                                    logger.info("reload {}", t.getId());
                                }
                                add(t, false);
                            }
                        });
            }
        }
    }
}
