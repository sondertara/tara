package com.sondertara.common.io.resource;

import java.util.List;

interface PathResourceFinder {
    List<Resource> find(ClassLoader classLoader, String pathPattern, PathMatcher pathMatcher);
}
