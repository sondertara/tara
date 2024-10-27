package com.sondertara.common.navigation;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.StreamUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Navigators {
    private Navigators(){

    }
    public static String getParentPath(String pathExpression, String separator) {
        return getParentPath(pathExpression, null, separator);
    }


    public static String getParentPath(String pathExpression, final String prefix, final String suffix) {
        Objects.requireNonNull(suffix);
        String[] segments = getPathSegments(pathExpression, prefix, suffix);
        if (ObjectUtils.length(segments) <= 1) {
            return null;
        }
        List<String> parentPath = Lists.asList(segments).subList(0, segments.length - 1);
        return StringUtils.iterateJoin("", prefix, suffix, parentPath);
    }

    public static String getLeaf(String pathExpression, final String separator) {
        return getLeaf(pathExpression, null, separator);
    }

    public static String getLeaf(String pathExpression, final String prefix, String suffix) {
        Objects.requireNonNull(suffix);
        String[] segments = getPathSegments(pathExpression, prefix, suffix);
        if (ObjectUtils.length(segments) < 1) {
            return null;
        }
        String leaf = segments[segments.length - 1];
        return leaf;
    }

    public static String[] getPathSegments(String expression, String separator) {
        return getPathSegments(expression, null, separator);
    }

    public static String[] getPathSegments(String expression, final String prefix, String suffix) {
        String[] segments = StringUtils.split(expression, suffix);
        if (StringUtils.isNotEmpty(prefix)) {
            segments = StreamUtils.of(segments)
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String input) {
                            return StringUtils.substring(input, prefix.length());
                        }
                    }).toArray(String[]::new);
        }
        return segments;
    }


}
