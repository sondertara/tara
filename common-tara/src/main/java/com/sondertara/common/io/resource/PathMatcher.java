package com.sondertara.common.io.resource;

import com.sondertara.common.function.Matcher;

public interface PathMatcher extends Matcher<String, Boolean> {
    @Override
    Boolean matches(String path);
}
