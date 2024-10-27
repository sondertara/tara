package com.sondertara.common.io.resource;

import com.sondertara.common.collection.Lists;

import java.util.List;

class ClassPathResourcesFinder implements PathResourceFinder {
    @Override
    public List<Resource> find(ClassLoader classLoader, String pathPattern, PathMatcher pathMatcher) {
        return Lists.immutableList();
    }
}
