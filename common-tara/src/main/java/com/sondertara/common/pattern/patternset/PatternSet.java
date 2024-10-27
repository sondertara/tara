package com.sondertara.common.pattern.patternset;

import com.sondertara.common.base.Nameable;
import com.sondertara.common.base.Named;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PatternSet<PatternEntry extends Named> implements Nameable {
    /**
     * 为PatternSet 命名
     */
    @Nullable
    private String name;

    /**
     * 解析 expression 时使用的分隔符, pattern 间的分隔符
     */
    private String separator;
    /**
     * 解析 expression 时，使用的排除符
     */
    private String excludeFlag;


    private String expression;

    private List<PatternEntry> includes = new ArrayList<>();
    private List<PatternEntry> excludes = new ArrayList<>();

    public List<PatternEntry> getIncludes() {
        return includes;
    }

    public void setIncludes(List<PatternEntry> includes) {
        this.includes = includes;
    }

    public List<PatternEntry> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<PatternEntry> excludes) {
        this.excludes = excludes;
    }

    public void addExclude(PatternEntry patternEntry) {
        if (patternEntry != null) {
            excludes.add(patternEntry);
        }
    }

    public void addInclude(PatternEntry patternEntry) {
        if (patternEntry != null) {
            includes.add(patternEntry);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getExcludeFlag() {
        return excludeFlag;
    }

    public void setExcludeFlag(String excludeFlag) {
        this.excludeFlag = excludeFlag;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
