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

package com.sondertara.common.io.file.filter;

import com.sondertara.common.io.file.FileFilters;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class AnyFileFilter implements CommonFileFilter {
    private FileFilter delegate;

    public AnyFileFilter(List<FileFilter> filters) {
        delegate = FileFilters.anyFileFilter(filters);
    }

    @Override
    public boolean accept(File e) {
        return delegate.accept(e);
    }

    @Override
    public boolean accept(File dir, String name) {
        return delegate.accept(new File(dir, name));
    }
}
