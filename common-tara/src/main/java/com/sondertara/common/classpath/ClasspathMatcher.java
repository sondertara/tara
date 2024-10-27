package com.sondertara.common.classpath;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.function.Matcher;
import com.sondertara.common.pattern.patternset.AntPathMatcher;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClasspathMatcher implements Matcher<String, Boolean> {
    private AntPathMatcher matcher;

    /**
     * @param classpath 可以是报名，类名
     * @return 是否匹配
     */
    @Override
    public Boolean matches(String classpath) {
        if (StringUtils.endsWith(classpath, ".class")) {
            classpath = StringUtils.substring(classpath, 0, classpath.length() - ".class".length());
        }
        classpath = StringUtils.replace(classpath, ".", "/");
        return matcher.matches(classpath);
    }

    public ClasspathMatcher(List<String> classPaths) {
        this(buildAntPathMatcher(classPaths));
    }

    public ClasspathMatcher(AntPathMatcher matcher) {
        this.matcher = matcher;
    }

    public ClasspathMatcher() {
    }

    public void setMatcher(AntPathMatcher matcher) {
        this.matcher = matcher;
    }

    public static AntPathMatcher buildAntPathMatcher(List<String> classPaths) {
        if (ObjectUtils.isEmpty(classPaths)) {
            return null;
        }
        String expression = StringUtils.join(";", StreamUtils.of(classPaths)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String classpath) {
                        classpath = StringUtils.replace(classpath, ".", "/");
                        if (StringUtils.startsWith(classpath, "~")) {
                            classpath = "!" + classpath.substring(1);
                        }
                        return classpath;
                    }
                })
                .collect(Collectors.toList()));

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        antPathMatcher.setPatternExpression(expression);
        antPathMatcher.setGlobal(true);
        return antPathMatcher;
    }


}
