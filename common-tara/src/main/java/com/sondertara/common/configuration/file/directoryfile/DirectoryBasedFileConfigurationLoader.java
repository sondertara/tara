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

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.configuration.AbstractConfigurationLoader;
import com.sondertara.common.configuration.Configuration;
import com.sondertara.common.configuration.InputStreamConfigurationParser;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.file.Filenames;
import com.sondertara.common.io.file.Files;
import com.sondertara.common.io.file.filter.AllFileFilter;
import com.sondertara.common.io.file.filter.IsFileFilter;
import com.sondertara.common.io.file.filter.ReadableFileFilter;
import com.sondertara.common.io.resource.DirectoryBasedFileResourceLoader;
import com.sondertara.common.io.resource.FileResource;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirectoryBasedFileConfigurationLoader<T extends Configuration> extends AbstractConfigurationLoader<T> {
    private DirectoryBasedFileResourceLoader resourceLoader;
    private InputStreamConfigurationParser<T> configurationParser;

    private final List<FileFilter> filters = new ArrayList<>();

    /**
     * Get a configuration id by the filename
     */
    private Function<String, String> configurationIdSupplier;
    /**
     * Get a filename configuration id
     */
    private Function<String, String> filenameSupplier;


    public DirectoryBasedFileConfigurationLoader() {
        this.filters.add(new ReadableFileFilter());
        this.filters.add(new IsFileFilter());
    }

    public void setDirectory(String directory) {
        resourceLoader = new DirectoryBasedFileResourceLoader(directory);
    }

    public void setConfigurationIdSupplier(Function<String, String> configurationIdSupplier) {
        this.configurationIdSupplier = configurationIdSupplier;
    }

    public void setFilenameSupplier(Function<String, String> filenameSupplier) {
        this.filenameSupplier = filenameSupplier;
    }

    public void setConfigurationParser(InputStreamConfigurationParser<T> configurationParser) {
        this.configurationParser = configurationParser;
    }

    public void addFilters(FileFilter... f) {
        CollectionUtils.addAll(this.filters, f);
    }

    @Override
    public T load(String id) {
        FileResource fileResource = resourceLoader.loadResource(filenameSupplier.apply(id));
        InputStream inputStream = null;
        T configuration = null;
        try {
            inputStream = fileResource.getInputStream();
            configuration = configurationParser.parse(inputStream);
            configuration.setId(id);
        } catch (Throwable ex) {
            Logger logger = Loggers.getLogger(getClass());
            logger.info("Error occur when load configuration: {}", id);
        } finally {
            IOUtils.close(inputStream);
        }
        return configuration;
    }

    public Map<String, Long> scanConfigurationFileModifiedTimes() {
        return resourceLoader.listFiles().stream()
                .filter(new AllFileFilter(this.filters))
                .collect(Collectors.toMap(file -> configurationIdSupplier.apply(Filenames.extractFilename(Files.getCanonicalPath(file), false))
                        ,
                        file -> file.lastModified(),(k1, k2)->k1, HashMap::new));
    }
}
