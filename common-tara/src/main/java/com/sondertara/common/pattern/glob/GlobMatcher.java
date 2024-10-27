package com.sondertara.common.pattern.glob;


import com.sondertara.common.base.Assert;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.io.resource.PathMatcher;
import com.sondertara.common.pattern.AbstractPatternMatcher;

/**
 *  *
 * <a href="https://mincong.io/2019/04/16/glob-expression-understanding/">glob expression</a>
 */
public class GlobMatcher extends AbstractPatternMatcher implements PathMatcher {
    private String globPattern;
    private GlobPattern glob;
    @Override
    public void setPatternExpression(String patternExpression) {
        this.globPattern = patternExpression;
    }

    @Override
    public Boolean matches(String string) {
        Assert.notEmpty(string, "the string is null or empty");
        Assert.notEmpty(globPattern, "the regexp is null or empty");

        if (glob == null) {
            if (trimPattern) {
                globPattern = StringUtils.trim(globPattern);
                if (StringUtils.isEmpty(globPattern)) {
                    throw new IllegalArgumentException("illegal regexp pattern");
                }
            }

            glob = new GlobPattern(globPattern);
        }

        if (trimPattern) {
            string = StringUtils.trim(string);
        }
        return glob.matches(string);
    }
}
