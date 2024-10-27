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

package com.sondertara.common.configuration.file.directoryfile;

import com.sondertara.common.base.Assert;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.diff.MapDiffResult;
import com.sondertara.common.configuration.AbstractConfigurationRepository;
import com.sondertara.common.configuration.Configuration;
import com.sondertara.common.io.file.Files;
import com.sondertara.common.lifecycle.InitializationException;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * multiple configuration file in one directory, every configuration will be load as a configurationlangx
 *
 * @param <T>
 */
public class DirectoryBasedFileConfigurationRepository<T extends Configuration> extends AbstractConfigurationRepository<T, DirectoryBasedFileConfigurationLoader<T>, DirectoryBasedFileConfigurationWriter<T>> {

    private String directory;

    private Map<String, Long> lastModifiedTimeMap = new HashMap<>(16);


    public void setDirectory(String directory) {
        this.directory = directory;
    }


    @Override
    public void init() throws InitializationException {
        if (!inited) {
            super.init();
            Objects.requireNonNull(loader, "the configuration load is null");
             Assert.isTrue(StringUtils.isNotBlank(directory), "directory is null");
            Logger logger = Loggers.getLogger(getClass());
            if (!Files.exists(new File(directory))) {
                logger.warn("Can't find a directory : {}, will create it", new File(directory).getAbsoluteFile());
                Files.makeDirs(directory);
            }
            loader.setDirectory(directory);

            if (writer == null) {
                logger.warn("The writer is not specified for the repository ({}), will disable write configuration to storage", getName());
            } else {
                writer.setDirectory(directory);
            }
            // enable refresh
            if (reloadIntervalInSeconds > 1) {
                logger.info("The configuration refresh task is disabled for repository: {}", getName());
            }
            inited = true;

        }
    }

    @Override
    public void reload() {
        Map<String, Long> modifiedTimeMap = loader.scanConfigurationFileModifiedTimes();
        try {
            MapDiffResult<String, Long> lastModifiedDiffResult = CollectionUtils.diff(lastModifiedTimeMap, modifiedTimeMap);
            CollectionUtils.forEach(lastModifiedDiffResult.getRemoves(), new BiConsumer<String, Long>() {
                @Override
                public void accept(String id, Long lastModified) {
                    removeById(id, false);
                }
            });
            CollectionUtils.forEach(lastModifiedDiffResult.getUpdates(), new BiConsumer<String, Long>() {
                @Override
                public void accept(String id, Long lastModified) {
                    T configurationInStorage = loader.load(id);
                    T configurationInCache = getById(id);
                    if (configurationInCache == null) {
                        add(configurationInStorage, false);
                    } else if (!configurationInCache.equals(configurationInStorage)) {
                        update(configurationInStorage, false);
                    }
                }
            });
            CollectionUtils.forEach(lastModifiedDiffResult.getAdds(), new BiConsumer<String, Long>() {
                @Override
                public void accept(String id, Long lastModified) {
                    T configurationInStorage = loader.load(id);
                    T configurationInCache = getById(id);
                    if (configurationInCache == null) {
                        add(configurationInStorage, false);
                    } else if (!configurationInCache.equals(configurationInStorage)) {
                        update(configurationInStorage, false);
                    }
                }
            });
        } finally {
            lastModifiedTimeMap = modifiedTimeMap;
        }
    }

}
