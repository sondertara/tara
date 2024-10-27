package com.sondertara.common.regex;

import com.sondertara.common.base.Named;

import java.util.function.BiFunction;

/**
 *  * A factory to create a regexp
 */
public interface RegexpEngine extends Named, BiFunction<String, Option, Regexp> {
    /**
     * 创建 Regexp实例
     * @param pattern 正则表达式
     * @param option 选项
     * @return 创建的正则表达式
     */
    @Override
    Regexp apply(String pattern, Option option);
}
