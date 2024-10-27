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

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.configuration.Configuration;
import com.sondertara.common.configuration.ConfigurationSerializer;
import com.sondertara.common.configuration.ConfigurationWriter;
import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.file.Files;
import com.sondertara.common.io.file.filter.WriteableFileFilter;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

public class DirectoryBasedFileConfigurationWriter<T extends Configuration> implements ConfigurationWriter<T> {

    private String directory;

    /**
     * serialize a configurition to string
     */
    private ConfigurationSerializer<T, String> configurationSerializer;

    /**
     * Get a filename by configuration id
     */
    private Function<String, String> filenameSupplier;
    /**
     * the configuration file's encoding
     */
    private Charset encoding = StandardCharsets.UTF_8;

    @Override
    public void write(T configuration) {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(directory);
        String configString = configurationSerializer.serialize(configuration);
        if (StringUtils.isEmpty(configString)) {
            return;
        }
        String filePath = getConfigurationFilePath(configuration.getId());
        BufferedOutputStream outputStream = null;
        try {
            if (Files.makeFile(filePath)) {
                outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                IOUtils.write(configString, outputStream, encoding);
            } else {
                Logger logger = Loggers.getLogger(getClass());
                logger.warn("write configuration to file fail, file: {}, configuration: {}", filePath, configuration);
            }
        } catch (IOException ex) {
            throw ExceptionUtils.wrapAsRuntimeException(ex);
        } finally {
            IOUtils.close(outputStream);
        }

    }

    public void setEncoding(String encoding) {
        this.encoding = Charset.forName(encoding);
    }

    public void setDirectory(String directory) {
        Objects.requireNonNull(directory);

        WriteableFileFilter writeableFileFilter = new WriteableFileFilter();
        boolean accept = writeableFileFilter.accept(new File(directory));
        Valid.isTrue(accept, "directory: %s is not writable", directory);
        this.directory = directory;
    }

    public void setConfigurationSerializer(ConfigurationSerializer<T, String> configurationSerializer) {
        this.configurationSerializer = configurationSerializer;
    }

    public void setFilenameSupplier(Function<String, String> filenameSupplier) {
        this.filenameSupplier = filenameSupplier;
    }

    @Override
    public boolean isSupportsWrite() {
        return true;
    }

    @Override
    public boolean isSupportsRewrite() {
        return true;
    }

    @Override
    public void rewrite(T configuration) {
        remove(configuration.getId());
        write(configuration);
    }

    @Override
    public boolean isSupportsRemove() {
        return true;
    }

    @Override
    public void remove(String id) {
        String filePath = getConfigurationFilePath(id);
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                Loggers.getLogger(this.getClass()).error("delete file failed: {}", filePath);
            }
        }
    }

    private String getConfigurationFilePath(String configurationId) {
        return directory + File.separator + filenameSupplier.apply(configurationId);
    }
}
