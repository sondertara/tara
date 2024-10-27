/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sondertara.common.configuration;

import com.sondertara.common.annotation.NullableIf;
import com.sondertara.common.cache.Cache;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.concurrent.DefaultThreadFactory;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.timing.timer.HashedWheelTimer;
import com.sondertara.common.timing.timer.Timeout;
import com.sondertara.common.timing.timer.Timer;
import com.sondertara.common.timing.timer.TimerTask;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("unchecked")
public abstract class AbstractConfigurationRepository<T extends Configuration, Loader extends ConfigurationLoader<T>, Writer extends ConfigurationWriter<T>> extends BaseConfigurationRepository<T,Loader,Writer> {

    @NonNull
    protected Cache<String, T> cache;

    /**
     * units: seconds
     * scan interval, if <=0, will not refresh
     */
    protected int reloadIntervalInSeconds = -1;

    public void setReloadIntervalInSeconds(int reloadIntervalInSeconds) {
        this.reloadIntervalInSeconds = reloadIntervalInSeconds;
    }

    public void setCache(Cache<String, T> cache) {
        this.cache = cache;
    }

    @NullableIf("reloadIntervalInSeconds>0")
    private Timer timer;

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    @Override
    protected void doStart() {
        if (!inited) {
            init();
        }
        Objects.requireNonNull(cache);
        final Logger logger = Loggers.getLogger(getClass());
        logger.info("Startup configuration repository: {}", getName());

        if (reloadIntervalInSeconds > 0) {
            try {
                reload();
            } catch (Throwable ex) {
                logger.warn(ex.getMessage(), ex);
            }
            if (timer == null) {
                logger.warn("The timer is not specified for the repository ({}) , will use a simple timer", getName());
                timer = new HashedWheelTimer(new  DefaultThreadFactory("Configuration", true), 50, TimeUnit.MILLISECONDS);
            }

            timer.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    try {
                        reload();
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    } finally {
                        if (isRunning()) {
                            timer.newTimeout(this, reloadIntervalInSeconds, TimeUnit.SECONDS);
                        }
                    }
                }
            }, reloadIntervalInSeconds, TimeUnit.SECONDS);
        } else {
            reload();
        }
    }

    @Override
    protected void doStop() {
        Logger logger = Loggers.getLogger(getClass());
        logger.info("Shutdown configuration repository: {}", getName());
        cache.clear();
    }


    @Override
    public T getById(String id) {
        return cache.get(id);
    }


    @Override
    public void removeById(String id, boolean sync) {
        T configuration = cache.get(id);
        if (configuration != null) {
            logMutation(ConfigurationEventType.REMOVE, configuration);
            if (sync && writer != null && writer.isSupportsRemove()) {
                writer.remove(id);
            }
            cache.remove(id);
            if (eventPublisher != null && eventFactory != null) {
                eventPublisher.publish(eventFactory.createEvent(ConfigurationEventType.ADD, configuration));
            }
        }
    }

    @Override
    public T add(T configuration, boolean sync) {
        if (isRunning()) {
            logMutation(ConfigurationEventType.ADD, configuration);
            if (sync && writer != null && writer.isSupportsWrite()) {
                writer.write(configuration);
            }
            cache.put(configuration.getId(), configuration);
            if (eventPublisher != null && eventFactory != null) {
                eventPublisher.publish(eventFactory.createEvent(ConfigurationEventType.ADD, configuration));
            }
        }
        return configuration;
    }

    @Override
    public void update(T configuration, boolean sync) {
        if (isRunning()) {
            logMutation(ConfigurationEventType.UPDATE, configuration);
            if (sync && writer != null && writer.isSupportsRewrite()) {
                writer.rewrite(configuration);
            }
            cache.put(configuration.getId(), configuration);
            if (eventPublisher != null && eventFactory != null) {
                eventPublisher.publish(eventFactory.createEvent(ConfigurationEventType.UPDATE, configuration));
            }
        }
    }

    @Override
    protected void doInit() {
        Objects.requireNonNull(getName(), "Repository has no named");
        Logger logger = Loggers.getLogger(getClass());
        logger.info("Initial configuration repository: {}", getName());
    }

    public Map<String, T> getAll() {
        return Maps.newImmutableMap(cache.toMap());
    }

}
