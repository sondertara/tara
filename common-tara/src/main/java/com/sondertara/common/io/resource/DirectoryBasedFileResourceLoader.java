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

package com.sondertara.common.io.resource;

import com.sondertara.common.base.Assert;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.io.file.filter.ExistsFileFilter;
import com.sondertara.common.io.file.filter.IsDirectoryFileFilter;
import com.sondertara.common.io.file.filter.ReadableFileFilter;
import com.sondertara.common.text.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirectoryBasedFileResourceLoader implements ResourceLoader {
    private String directory;

    private ResourceLoader delegate;

    public DirectoryBasedFileResourceLoader(String directory) {
        this(directory, null);
    }

    public DirectoryBasedFileResourceLoader(String directory, ClassLoader classLoader) {
        Objects.requireNonNull(directory, "directory is null");
         Assert.isTrue(new ExistsFileFilter().accept(new File(directory)), StringUtils.format("directory {} is not exists", directory));
         Assert.isTrue(new IsDirectoryFileFilter().accept(new File(directory)), StringUtils.format("directory {} is not a directory", directory));
         Assert.isTrue(new ReadableFileFilter().accept(new File(directory)), StringUtils.format("directory {} is not readable", directory));
        this.directory = directory;

        this.delegate = new DefaultResourceLoader(classLoader);
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public FileResource loadResource(String filename) {
        return delegate.loadResource(FileResource.PREFIX + directory + File.separator + filename);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

    public List<File> listFiles() {
        File dir = new File(directory);
        return StreamUtils.of(dir.listFiles()).map(File::getAbsoluteFile).collect(Collectors.toList());
    }

    public List<File> listFiles(FileFilter fileFilter) {
        File dir = new File(directory);
        return StreamUtils.of(dir.listFiles(fileFilter)).map(new Function<File, File>() {
            @Override
            public File apply(File file) {
                return file.getAbsoluteFile();
            }
        }).collect(Collectors.toList());
    }
}
