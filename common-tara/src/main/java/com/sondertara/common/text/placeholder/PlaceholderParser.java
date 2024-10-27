package com.sondertara.common.text.placeholder;

import com.sondertara.common.function.Parser;

public interface PlaceholderParser extends Parser<String, String> {
    /**
     * 解析变量
     * @param variable 变量名
     * @return 变量值
     */
    @Override
    String parse(String variable);
}
